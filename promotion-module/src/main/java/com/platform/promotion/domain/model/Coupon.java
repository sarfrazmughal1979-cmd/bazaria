package com.platform.promotion.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "coupons", indexes = {
        @Index(name = "idx_coupon_code", columnList = "code", unique = true),
        @Index(name = "idx_coupon_active_dates", columnList = "is_active, start_date, end_date"),
        @Index(name = "idx_coupon_vendor", columnList = "vendor_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends AuditableEntity {

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;   // PERCENTAGE or FIXED

    @Column(name = "discount_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal discountValue;

    @Column(name = "min_order_amount", precision = 19, scale = 4)
    private BigDecimal minOrderAmount;

    @Column(name = "max_discount_amount", precision = 19, scale = 4)
    private BigDecimal maxDiscountAmount;  // for percentage coupons

    @Column(name = "usage_limit")
    private Integer usageLimit;            // total times this coupon can be used

    @Column(name = "used_count")
    @Builder.Default
    private int usedCount = 0;

    @Column(name = "per_user_limit")
    private Integer perUserLimit;          // times per customer

    @Column(name = "vendor_id")
    private UUID vendorId;                 // null = platform-wide

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    // Domain methods
    public BigDecimal calculateDiscount(BigDecimal subtotal) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if (discountType == DiscountType.PERCENTAGE) {
            discount = subtotal.multiply(discountValue.divide(BigDecimal.valueOf(100)));
            if (maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0) {
                discount = maxDiscountAmount;
            }
        } else { // FIXED
            discount = discountValue;
        }

        // Discount cannot exceed subtotal
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }
        return discount;
    }

    public boolean isCurrentlyValid() {
        Instant now = Instant.now();
        if (!active) return false;
        if (startDate != null && now.isBefore(startDate)) return false;
        if (endDate != null && now.isAfter(endDate)) return false;
        if (usageLimit != null && usedCount >= usageLimit) return false;
        return true;
    }
}