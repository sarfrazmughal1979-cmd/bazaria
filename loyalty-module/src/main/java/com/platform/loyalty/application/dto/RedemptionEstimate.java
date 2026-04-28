package com.platform.loyalty.application.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RedemptionEstimate {
    private long requestedPoints;
    private BigDecimal maxDiscount;
    private BigDecimal orderSubtotal;
    private BigDecimal applicableDiscount;
}