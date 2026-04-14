package com.platform.payment.application.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackDTO {
    private String gatewayTransactionId;
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private boolean success;
    private String failureReason;
    private String rawData;
    private String signature;
}