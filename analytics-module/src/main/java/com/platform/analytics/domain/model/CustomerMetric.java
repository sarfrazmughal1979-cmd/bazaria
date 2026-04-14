package com.platform.analytics.domain.model;

import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "customer_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerMetric extends AuditableEntity {

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "total_orders")
    private long totalOrders;

    @Column(name = "total_spent", precision = 19, scale = 4)
    private BigDecimal totalSpent;

    @Column(name = "last_order_date")
    private LocalDate lastOrderDate;

    @Column(name = "is_active")
    private boolean active;  // ordered within last 90 days
}