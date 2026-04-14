package com.platform.analytics.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPerformance {
    private UUID productId;
    private String productName;
    private long views;
    private long addToCarts;
    private long orders;
    private long quantitySold;
    private BigDecimal revenue;
    private BigDecimal conversionRate;
}