package com.platform.fraud.application.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FraudEvaluationResponse {
    private String orderId;
    private int riskScore;
    private String status;   // APPROVED, REVIEW, REJECTED
    private String reasons;
}