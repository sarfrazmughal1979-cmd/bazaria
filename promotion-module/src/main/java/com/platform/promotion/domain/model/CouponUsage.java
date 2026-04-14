package com.platform.promotion.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "coupon_usages", indexes = {
        @Index(name = "idx_coupon_usage_coupon", columnList = "coupon_id"),
        @Index(name = "idx_coupon_usage_customer", columnList = "customer_id"),
        @Index(name = "idx_coupon_usage_order", columnList = "order_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUsage extends BaseEntity {

    @Column(name = "coupon_id", nullable = false)
    private UUID couponId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "discount_amount", precision = 19, scale = 4)
    private java.math.BigDecimal discountAmount;

    @Column(name = "used_at", nullable = false)
    private Instant usedAt;
}