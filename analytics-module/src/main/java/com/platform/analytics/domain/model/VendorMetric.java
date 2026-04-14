package com.platform.analytics.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vendor_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorMetric extends BaseEntity {

    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "total_products")
    private int totalProducts;

    @Column(name = "active_products")
    private int activeProducts;

    @Column(name = "total_orders")
    private long totalOrders;

    @Column(name = "total_revenue", precision = 19, scale = 4)
    private BigDecimal totalRevenue;

    @Column(name = "total_commission", precision = 19, scale = 4)
    private BigDecimal totalCommission;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "on_time_delivery_rate", precision = 5, scale = 2)
    private BigDecimal onTimeDeliveryRate;

    @Column(name = "dispute_rate", precision = 5, scale = 2)
    private BigDecimal disputeRate;
}
