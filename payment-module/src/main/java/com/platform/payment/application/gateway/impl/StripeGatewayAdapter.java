package com.platform.payment.application.gateway.impl;

import com.platform.payment.application.dto.InitiatePaymentResponse;
import com.platform.payment.application.dto.PaymentCallbackDTO;
import com.platform.payment.application.gateway.PaymentGatewayAdapter;
import com.platform.payment.domain.model.Payment;
import com.platform.payment.domain.model.PaymentStatus;
import com.platform.payment.domain.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component("stripeGatewayAdapter")
public class StripeGatewayAdapter implements PaymentGatewayAdapter {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.secret-key:sk_test_placeholder}")
    private String stripeSecretKey;

    @Value("${stripe.webhook-secret:whsec_placeholder}")
    private String webhookSecret;

    public StripeGatewayAdapter(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public InitiatePaymentResponse initiate(Payment payment) {
        Stripe.apiKey = stripeSecretKey;
        try {
            long amountInPaisa = payment.getAmount().getAmount().multiply(BigDecimal.valueOf(100)).longValue();
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInPaisa)
                    .setCurrency(payment.getAmount().getCurrencyCode().toLowerCase())
                    .setDescription("Order " + payment.getOrderId())
                    .putMetadata("order_id", payment.getOrderId().toString())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            return InitiatePaymentResponse.builder()
                    .transactionId(intent.getId())
                    .redirectUrl(intent.getClientSecret())
                    .rawResponse(intent.toJson())
                    .build();
        } catch (StripeException e) {
            log.error("Stripe payment initiation failed: {}", e.getMessage());
            throw new RuntimeException("Stripe API error: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyCallback(PaymentCallbackDTO callback) {
        return callback.isSuccess();
    }

    @Override
    public String getPaymentStatus(String gatewayTransactionId) {
        Stripe.apiKey = stripeSecretKey;
        try {
            PaymentIntent intent = PaymentIntent.retrieve(gatewayTransactionId);
            if ("succeeded".equals(intent.getStatus())) return "COMPLETED";
            if ("processing".equals(intent.getStatus())) return "PENDING";
            if ("requires_payment_method".equals(intent.getStatus()) ||
                    "requires_action".equals(intent.getStatus())) return "PENDING";
            return "FAILED";
        } catch (StripeException e) {
            log.error("Stripe status check failed for PI {}", gatewayTransactionId, e);
            return "FAILED";
        }
    }

    @Override
    public String refund(Payment payment, BigDecimal amount) {
        Stripe.apiKey = stripeSecretKey;
        try {
            long amountInPaisa = amount.multiply(BigDecimal.valueOf(100)).longValue();
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getGatewayTransactionId())
                    .setAmount(amountInPaisa)
                    .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                    .build();
            Refund refund = Refund.create(params);
            log.info("Stripe refund created: id={}, amount={}", refund.getId(), amount);
            return refund.getId();
        } catch (StripeException e) {
            log.error("Stripe refund failed: {}", e.getMessage());
            throw new RuntimeException("Stripe refund error: " + e.getMessage());
        }
    }

    @Override public String getGatewayName() { return "STRIPE"; }
    @Override public boolean supportsWebhook() { return true; }

    @Override
    public void processWebhook(String payload, String signature) {
        try {
            com.stripe.model.Event event = Webhook.constructEvent(payload, signature, webhookSecret);
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    PaymentIntent pi = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (pi != null) {
                        paymentRepository.findByGatewayTransactionId(pi.getId()).ifPresent(p -> {
                            p.setStatus(PaymentStatus.COMPLETED);
                            paymentRepository.save(p);
                            log.info("Stripe webhook: payment {} completed", p.getId());
                        });
                    }
                    break;
                case "payment_intent.payment_failed":
                    log.warn("Stripe webhook: payment failed for event {}", event.getId());
                    break;
                case "charge.refunded":
                    log.info("Stripe webhook: refund processed for event {}", event.getId());
                    break;
                default:
                    log.debug("Stripe webhook: unhandled event type {}", event.getType());
            }
        } catch (com.stripe.exception.SignatureVerificationException e) {
            log.error("Stripe webhook signature verification failed", e);
        } catch (Exception e) {
            log.error("Stripe webhook processing error", e);
        }
    }
}