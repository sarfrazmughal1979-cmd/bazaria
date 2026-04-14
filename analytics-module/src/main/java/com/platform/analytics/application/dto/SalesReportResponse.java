package com.platform.analytics.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportResponse {
    private String period;
    private BigDecimal totalRevenue;
    private long totalOrders;
    private BigDecimal totalCommission;
    private BigDecimal totalTax;
    private BigDecimal averageOrderValue;
    private List<DailyBreakdown> breakdown;

    @Data
    @Builder
    public static class DailyBreakdown {
        private String date;
        private BigDecimal revenue;
        private long orders;
    }
}