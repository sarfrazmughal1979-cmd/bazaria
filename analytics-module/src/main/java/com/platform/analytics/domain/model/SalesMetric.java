package com.platform.analytics.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sales_metrics", indexes = {
    @Index(name = "idx_sales_date", columnList = "metric_date"),
    @Index(name = "idx_sales_vendor", columnList = "vendor_id"),
    @Index(name = "idx_sales_period", columnList = "period_type, metric_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesMetric extends BaseEntity {

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "period_type", nullable = false, length = 10)
    private String periodType;  // DAY, WEEK, MONTH, YEAR

    @Column(name = "vendor_id")
    private UUID vendorId;      // null = platform-wide

    @Column(name = "total_orders")
    private long totalOrders;

    @Column(name = "total_revenue", precision = 19, scale = 4)
    private BigDecimal totalRevenue;

    @Column(name = "total_commission", precision = 19, scale = 4)
    private BigDecimal totalCommission;

    @Column(name = "total_tax", precision = 19, scale = 4)
    private BigDecimal totalTax;

    @Column(name = "average_order_value", precision = 19, scale = 4)
    private BigDecimal averageOrderValue;

    @Column(name = "unique_customers")
    private long uniqueCustomers;

    @Column(name = "items_sold")
    private long itemsSold;
}