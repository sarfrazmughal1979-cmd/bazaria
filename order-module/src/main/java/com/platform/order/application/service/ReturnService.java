package com.platform.order.application.service;

import com.platform.core.client.RestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.order.application.dto.ReturnRequestDTO;
import com.platform.order.domain.model.ReturnRequest;
import com.platform.order.domain.model.ReturnStatus;
import com.platform.order.domain.repository.ReturnRequestRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ReturnRequestRepository returnRepository;
    private final RestClientFactory restClientFactory;

    @Value("${module.payment.url:http://localhost:8080}")
    private String paymentBaseUrl;

    private RestClient paymentRestClient;

    @PostConstruct
    public void init() {
        paymentRestClient = restClientFactory.create(paymentBaseUrl, 10);
    }

    @Transactional
    public ReturnRequest requestReturn(ReturnRequestDTO request) {
        // Validate order belongs to customer (call Order module REST) – simplified
        ReturnRequest rr = ReturnRequest.builder()
            .orderId(request.getOrderId())
            .subOrderId(request.getSubOrderId())
            .customerId(request.getCustomerId())
            .reason(request.getReason())
            .description(request.getDescription())
            .status(ReturnStatus.PENDING)
            .build();
        return returnRepository.save(rr);
    }

    @Transactional
    public void approveReturn(UUID returnId, BigDecimal refundAmount) {
        ReturnRequest rr = returnRepository.findById(returnId)
            .orElseThrow(() -> new ResourceNotFoundException("ReturnRequest", "id", returnId));
        rr.setStatus(ReturnStatus.APPROVED);
        // Trigger refund via Payment module REST
        paymentRestClient.post("/api/internal/payments/refund", Map.of("orderId", rr.getOrderId(), "amount", refundAmount), Void.class);
        returnRepository.save(rr);
    }
}
