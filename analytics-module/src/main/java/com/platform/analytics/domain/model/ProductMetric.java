package com.platform.analytics.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "product_metrics", indexes = {
    @Index(name = "idx_product_metric_product", columnList = "product_id"),
    @Index(name = "idx_product_metric_date", columnList = "metric_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMetric extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "views")
    private long views;

    @Column(name = "add_to_carts")
    private long addToCarts;

    @Column(name = "orders")
    private long orders;

    @Column(name = "quantity_sold")
    private long quantitySold;

    @Column(name = "revenue", precision = 19, scale = 4)
    private BigDecimal revenue;

    @Column(name = "conversion_rate", precision = 5, scale = 2)
    private BigDecimal conversionRate;  // (orders / views) * 100

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;
}