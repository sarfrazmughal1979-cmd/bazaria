package com.platform.promotion.application.service;

import com.platform.promotion.domain.model.Coupon;
import com.platform.promotion.domain.repository.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponValidationService {

    private final CouponUsageRepository couponUsageRepository;

    public boolean isValid(Coupon coupon, BigDecimal subtotal, UUID customerId) {
        // Check date range
        Instant now = Instant.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) return false;
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) return false;

        // Check usage limit
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) return false;

        // Check minimum order amount
        if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) return false;

        // Check per‑customer limit
        if (coupon.getPerUserLimit() != null && customerId != null) {
            long userUsage = couponUsageRepository.countByCouponIdAndCustomerId(coupon.getId(), customerId);
            if (userUsage >= coupon.getPerUserLimit()) return false;
        }

        return true;
    }
}