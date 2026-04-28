package com.platform.payment.application.service;

import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.domain.Money;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.event.PaymentCompletedEvent;
import com.platform.core.event.PaymentFailedEvent;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.payment.application.dto.*;
import com.platform.payment.application.gateway.PaymentGatewayAdapter;
import com.platform.payment.application.gateway.PaymentGatewayFactory;
import com.platform.payment.domain.model.*;
import com.platform.payment.domain.repository.PaymentRepository;
import com.platform.payment.domain.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentGatewayFactory gatewayFactory;
    private final DomainEventPublisher eventPublisher;
    private final RedissonClient redissonClient;
    private final RestClientFactory restClientFactory;

    @Value("${module.order.url:http://localhost:8080}")
    private String orderBaseUrl;

    private ResilientRestClient orderRestClient;

    @PostConstruct
    public void init() {
        orderRestClient = restClientFactory.create(orderBaseUrl, 10);
    }

    // DTO for order info response
    private record OrderInfo(UUID orderId, String orderNumber, UUID customerId,
                             BigDecimal totalAmount, String currency, String status) {}

    // ============================================================
    // Initiate Payment
    // ============================================================

    @Transactional
    public PaymentResponse initiatePayment(InitiatePaymentRequest request) {
        // Idempotency check
        String lockKey = "payment:order:" + request.getOrderId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(5, 30, TimeUnit.SECONDS)) {
                throw new BusinessException("PAYMENT_IN_PROGRESS",
                        "Payment is already being processed for this order");
            }

            // Check if payment already exists for this order
            if (paymentRepository.existsByOrderIdAndStatus(request.getOrderId(), PaymentStatus.COMPLETED)) {
                throw new BusinessException("ALREADY_PAID", "Order is already paid");
            }

            // Verify order amount via REST call to Order module
            OrderInfo orderInfo = orderRestClient.get(
                    "/api/v1/orders/{orderId}/info-mini", OrderInfo.class, request.getOrderId());
            if (orderInfo == null) {
                throw new BusinessException("ORDER_NOT_FOUND", "Order not found");
            }
            if (orderInfo.totalAmount().compareTo(request.getAmount()) != 0) {
                throw new BusinessException("AMOUNT_MISMATCH",
                        "Order amount does not match payment amount");
            }

            // Create payment record
            Money expectedAmount = Money.of(request.getAmount(), request.getCurrency());
            Payment payment = Payment.builder()
                    .orderId(request.getOrderId())
                    .customerId(request.getCustomerId())
                    .amount(expectedAmount)
                    .gateway(PaymentGateway.valueOf(request.getGateway().toUpperCase()))
                    .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()))
                    .status(PaymentStatus.INITIATED)
                    .build();
            payment = paymentRepository.save(payment);

            // Initiate with payment gateway
            PaymentGatewayAdapter adapter = gatewayFactory.getAdapter(payment.getGateway());
            InitiatePaymentResponse gatewayResponse = adapter.initiate(payment);

            payment.setGatewayTransactionId(gatewayResponse.getTransactionId());
            payment.setGatewayRedirectUrl(gatewayResponse.getRedirectUrl());
            payment.setStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);

            // Record transaction
            Transaction transaction = Transaction.builder()
                    .payment(payment)
                    .type("INITIATE")
                    .amount(payment.getAmount())
                    .status("PENDING")
                    .gatewayResponse(gatewayResponse.getRawResponse())
                    .build();
            transactionRepository.save(transaction);

            return PaymentResponse.builder()
                    .paymentId(payment.getId())
                    .orderId(payment.getOrderId())
                    .amount(payment.getAmount().getAmount())
                    .currency(payment.getAmount().getCurrencyCode())
                    .gateway(payment.getGateway().name())
                    .paymentMethod(payment.getPaymentMethod().name())
                    .status(payment.getStatus().name())
                    .redirectUrl(gatewayResponse.getRedirectUrl())
                    .createdAt(payment.getCreatedAt())
                    .build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("PAYMENT_ERROR", "Payment processing interrupted");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // ============================================================
    // Handle Payment Callback (Webhook / Redirect)
    // ============================================================

    @Transactional
    public void handlePaymentCallback(PaymentCallbackDTO callback) {
        Payment payment = paymentRepository.findByGatewayTransactionId(callback.getGatewayTransactionId())
                .orElseThrow(() -> new BusinessException("PAYMENT_NOT_FOUND",
                        "Payment not found for transaction: " + callback.getGatewayTransactionId()));

        PaymentGatewayAdapter adapter = gatewayFactory.getAdapter(payment.getGateway());
        boolean verified = adapter.verifyCallback(callback);

        // Record callback transaction
        Transaction transaction = Transaction.builder()
                .payment(payment)
                .type("CALLBACK")
                .amount(payment.getAmount())
                .status(callback.isSuccess() ? "SUCCESS" : "FAILED")
                .gatewayResponse(callback.getRawData())
                .build();
        transactionRepository.save(transaction);

        if (verified && callback.isSuccess()) {
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);

            // Notify Order module via REST
            orderRestClient.post(
                    "/api/v1/orders/{orderId}/confirm?paymentId={paymentId}",
                    null, Void.class, payment.getOrderId(), payment.getId());

            // Publish event
            eventPublisher.publish(new PaymentCompletedEvent(
                    payment.getId().toString(),
                    payment.getOrderId().toString(),
                    payment.getAmount().getAmount(),
                    payment.getAmount().getCurrencyCode()
            ));

            log.info("Payment completed: order={}, payment={}", payment.getOrderId(), payment.getId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(callback.getFailureReason());
            paymentRepository.save(payment);

            eventPublisher.publish(new PaymentFailedEvent(
                    payment.getId().toString(),
                    payment.getOrderId().toString(),
                    payment.getAmount().getAmount(),
                    payment.getAmount().getCurrencyCode(),
                    callback.getFailureReason()
            ));

            log.warn("Payment failed: order={}, reason={}", payment.getOrderId(), callback.getFailureReason());
        }
    }

    // ============================================================
    // Query Payment Status
    // ============================================================

    @Transactional(readOnly = true)
    public String getPaymentStatus(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
        return payment.getStatus().name();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount().getAmount())
                .currency(payment.getAmount().getCurrencyCode())
                .gateway(payment.getGateway().name())
                .paymentMethod(payment.getPaymentMethod().name())
                .status(payment.getStatus().name())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    // ============================================================
    // Refund Payment
    // ============================================================

    @Transactional
    public void refund(UUID paymentId, BigDecimal amount, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessException("INVALID_PAYMENT_STATUS",
                    "Only completed payments can be refunded");
        }

        Money refundAmount = Money.of(amount, payment.getAmount().getCurrencyCode());
        if (refundAmount.isGreaterThan(payment.getAmount())) {
            throw new BusinessException("INVALID_REFUND_AMOUNT",
                    "Refund amount cannot exceed payment amount");
        }

        PaymentGatewayAdapter adapter = gatewayFactory.getAdapter(payment.getGateway());
        String refundId = adapter.refund(payment, amount);

        // Update payment status
        if (refundAmount.getAmount().compareTo(payment.getAmount().getAmount()) == 0) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }
        paymentRepository.save(payment);

        // Record refund transaction
        Transaction refundTransaction = Transaction.builder()
                .payment(payment)
                .type("REFUND")
                .amount(refundAmount)
                .status("SUCCESS")
                .gatewayResponse("Refund ID: " + refundId)
                .build();
        transactionRepository.save(refundTransaction);

        log.info("Refund processed: payment={}, amount={}, reason={}", paymentId, amount, reason);
    }

    // ============================================================
    // Scheduled: Release expired pending payments
    // ============================================================

    @Transactional
    public void expirePendingPayments() {
        Instant expiryThreshold = Instant.now().minusSeconds(30 * 60); // 30 minutes
        var pendingPayments = paymentRepository.findByStatusAndCreatedAtBefore(
                PaymentStatus.PENDING, expiryThreshold);
        for (Payment payment : pendingPayments) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Payment expired due to timeout");
            paymentRepository.save(payment);
            log.info("Expired pending payment: {}", payment.getId());
        }
    }

    /**
     * Process incoming webhook from payment gateway.
     *
     * @param gateway   the payment gateway name (e.g., "stripe", "bkash")
     * @param payload   raw JSON payload from the gateway
     * @param signature signature header for verification (if any)
     */
    @Transactional
    public void processWebhook(String gateway, String payload, String signature) {
        log.info("Processing webhook for gateway: {}", gateway);

        PaymentGatewayAdapter adapter = gatewayFactory.getAdapter(PaymentGateway.valueOf(gateway.toUpperCase()));

        if (!adapter.supportsWebhook()) {
            log.warn("Gateway {} does not support webhooks, ignoring", gateway);
            return;
        }

        adapter.processWebhook(payload, signature);
    }
}