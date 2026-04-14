package com.platform.payment.application.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentResponse {
    private String transactionId;
    private String redirectUrl;
    private String rawResponse;
}