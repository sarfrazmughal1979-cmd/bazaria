package com.platform.loyalty.application.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoyaltyAccountResponse {
    private long availablePoints;
    private long totalPointsEarned;
    private String tier;
}