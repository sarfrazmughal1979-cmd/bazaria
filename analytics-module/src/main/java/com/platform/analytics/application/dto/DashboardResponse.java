package com.platform.analytics.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private SummaryMetrics summary;
    private List<DailySales> salesTrend;
    private List<TopProduct> topProducts;
    private List<TopVendor> topVendors;

    @Data
    @Builder
    public static class SummaryMetrics {
        private BigDecimal totalRevenue;
        private long totalOrders;
        private long totalCustomers;
        private long totalProducts;
        private BigDecimal averageOrderValue;
    }

    @Data
    @Builder
    public static class DailySales {
        private String date;
        private BigDecimal revenue;
        private long orders;
    }

    @Data
    @Builder
    public static class TopProduct {
        private String productId;
        private String productName;
        private long quantitySold;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    public static class TopVendor {
        private String vendorId;
        private String shopName;
        private BigDecimal revenue;
        private long orders;
    }
}