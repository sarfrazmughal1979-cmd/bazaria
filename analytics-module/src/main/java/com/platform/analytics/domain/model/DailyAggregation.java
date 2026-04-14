package com.platform.analytics.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_aggregations", indexes = {
    @Index(name = "idx_daily_date", columnList = "aggregation_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyAggregation extends BaseEntity {

    @Column(name = "aggregation_date", nullable = false, unique = true)
    private LocalDate aggregationDate;

    @Column(name = "platform_revenue", precision = 19, scale = 4)
    private BigDecimal platformRevenue;

    @Column(name = "platform_orders")
    private long platformOrders;

    @Column(name = "new_customers")
    private long newCustomers;

    @Column(name = "active_vendors")
    private long activeVendors;

    @Column(name = "total_products_sold")
    private long totalProductsSold;
}