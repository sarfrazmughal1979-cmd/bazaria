package com.platform.support.application.service;

import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.dto.PagedResponse;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.event.DisputeOpenedEvent;
import com.platform.core.event.DisputeResolvedEvent;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.core.security.SecurityUtils;
import com.platform.support.application.dto.DisputeRequest;
import com.platform.support.application.dto.DisputeResponse;
import com.platform.support.application.mapper.SupportMapper;
import com.platform.support.domain.model.Dispute;
import com.platform.support.domain.model.DisputeStatus;
import com.platform.support.domain.repository.DisputeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final SupportMapper mapper;
    private final DomainEventPublisher eventPublisher;
    private final RestClientFactory restClientFactory;

    @Value("${module.iam.url:http://localhost:8080}")
    private String iamBaseUrl;

    @Value("${module.order.url:http://localhost:8080}")
    private String orderBaseUrl;

    @Value("${module.payment.url:http://localhost:8080}")
    private String paymentBaseUrl;

    private ResilientRestClient iamRestClient;
    private ResilientRestClient orderRestClient;
    private ResilientRestClient paymentRestClient;

    @PostConstruct
    public void init() {
        iamRestClient = restClientFactory.create(iamBaseUrl, 10);
        orderRestClient = restClientFactory.create(orderBaseUrl, 10);
        paymentRestClient = restClientFactory.create(paymentBaseUrl, 10);
    }

    // DTOs for REST responses
    private record OrderInfo(UUID orderId, String orderNumber, UUID customerId, UUID vendorId,
                             BigDecimal totalAmount, String currency, String status) {}
    private record SubOrderInfo(UUID subOrderId, UUID orderId, UUID vendorId, String status,
                                BigDecimal subtotal) {}
    private record UserInfo(UUID userId, String fullName, String email) {}
    private record VendorInfo(UUID vendorId, String shopName) {}

    @Transactional
    public DisputeResponse openDispute(DisputeRequest request) {
        UUID customerId = SecurityUtils.getCurrentUserId();

        // 1. Verify order belongs to customer via REST
        OrderInfo orderInfo = orderRestClient.get(
                "/api/v1/orders/{orderId}/info-mini", OrderInfo.class, request.getOrderId());
        if (orderInfo == null || !orderInfo.customerId().equals(customerId)) {
            throw new BusinessException("INVALID_ORDER", "Order not found or access denied");
        }

        // 2. Check if dispute already exists
        if (request.getSubOrderId() != null) {
            var existing = disputeRepository.findByOrderIdAndSubOrderId(request.getOrderId(), request.getSubOrderId());
            if (existing.isPresent()) {
                throw new BusinessException("DISPUTE_EXISTS", "A dispute already exists for this order");
            }
        }

        // 3. Get vendor ID from sub-order or order
        UUID vendorId;
        if (request.getSubOrderId() != null) {
            SubOrderInfo subOrder = orderRestClient.get(
                    "/api/v1/sub-orders/{subOrderId}/info-mini", SubOrderInfo.class, request.getSubOrderId());
            vendorId = subOrder.vendorId();
        } else {
            vendorId = orderInfo.vendorId();
        }

        // 4. Create dispute entity
        String disputeNumber = Dispute.generateDisputeNumber();
        Dispute dispute = mapper.toEntity(request, customerId, vendorId, disputeNumber);
        dispute.setDisputeNumber(disputeNumber);
        dispute.setCustomerId(customerId);
        dispute.setVendorId(vendorId);
        dispute.setOrderId(request.getOrderId());
        dispute.setSubOrderId(request.getSubOrderId());
        dispute.setStatus(DisputeStatus.PENDING);

        dispute = disputeRepository.save(dispute);

        // 5. Publish event
        eventPublisher.publish(new DisputeOpenedEvent(
                dispute.getId().toString(),
                dispute.getDisputeNumber(),
                request.getOrderId().toString(),
                customerId.toString(),
                vendorId.toString()));

        return enrichResponse(mapper.toResponse(dispute));
    }

    @Transactional
    public DisputeResponse resolveDispute(UUID disputeId, String resolution, BigDecimal amount, String adminNotes) {
        UUID adminId = SecurityUtils.getCurrentUserId();

        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute", "id", disputeId));

        dispute.resolve(resolution, amount, adminId);
        dispute.setAdminNotes(adminNotes);
        disputeRepository.save(dispute);

        // If resolution is refund, trigger payment refund via REST
        if (resolution.startsWith("REFUND") && amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            paymentRestClient.post("/api/internal/payments/refund",
                    new RefundRequest(dispute.getOrderId(), dispute.getSubOrderId(), amount, "Dispute resolution"),
                    Void.class);
        }

        eventPublisher.publish(new DisputeResolvedEvent(
                dispute.getId().toString(),
                dispute.getDisputeNumber(),
                resolution,
                amount));

        return enrichResponse(mapper.toResponse(dispute));
    }

    @Transactional(readOnly = true)
    public PagedResponse<DisputeResponse> getMyDisputes(Pageable pageable) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Page<Dispute> page = disputeRepository.findByCustomerId(userId, pageable);
        return PagedResponse.from(page.map(this::enrichResponseWithMapper));
    }

    @Transactional(readOnly = true)
    public PagedResponse<DisputeResponse> getVendorDisputes(Pageable pageable) {
        UUID vendorId = SecurityUtils.getCurrentVendorId()
                .orElseThrow(() -> new BusinessException("NOT_VENDOR", "Not a vendor"));
        Page<Dispute> page = disputeRepository.findByVendorId(vendorId, pageable);
        return PagedResponse.from(page.map(this::enrichResponseWithMapper));
    }

    @Transactional(readOnly = true)
    public PagedResponse<DisputeResponse> getPendingDisputes(Pageable pageable) {
        Page<Dispute> page = disputeRepository.findByStatus(DisputeStatus.PENDING, pageable);
        return PagedResponse.from(page.map(this::enrichResponseWithMapper));
    }

    // Helper methods
    private DisputeResponse enrichResponseWithMapper(Dispute dispute) {
        return enrichResponse(mapper.toResponse(dispute));
    }

    private DisputeResponse enrichResponse(DisputeResponse response) {
        // Add customer name via REST
        if (response.getCustomerId() != null) {
            try {
                UserInfo user = iamRestClient.get(
                        "/api/v1/users/{userId}/info-mini", UserInfo.class, response.getCustomerId());
                if (user != null) response.setCustomerName(user.fullName());
            } catch (Exception e) {
                log.warn("Failed to fetch customer name for {}", response.getCustomerId());
            }
        }
        // Add vendor name via REST
        if (response.getVendorId() != null) {
            try {
                VendorInfo vendor = iamRestClient.get(
                        "/api/v1/vendors/{vendorId}/info-mini", VendorInfo.class, response.getVendorId());
                if (vendor != null) response.setVendorName(vendor.shopName());
            } catch (Exception e) {
                log.warn("Failed to fetch vendor name for {}", response.getVendorId());
            }
        }
        return response;
    }

    // Request DTO for refund
    private record RefundRequest(UUID orderId, UUID subOrderId, BigDecimal amount, String reason) {}
}