package com.platform.analytics.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorPerformanceResponse {
    private String vendorId;
    private String shopName;
    private BigDecimal totalRevenue;
    private long totalOrders;
    private BigDecimal totalCommission;
    private BigDecimal averageRating;
    private BigDecimal onTimeDeliveryRate;
    private BigDecimal disputeRate;
    private List<ProductPerformance> topProducts;
}