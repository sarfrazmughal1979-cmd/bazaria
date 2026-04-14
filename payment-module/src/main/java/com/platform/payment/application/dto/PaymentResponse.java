package com.platform.payment.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private String currency;
    private String gateway;
    private String paymentMethod;
    private String status;
    private String redirectUrl;
    private String failureReason;
    private Instant createdAt;
}