package com.platform.promotion.application.service;

import com.platform.core.exception.BusinessException;
import com.platform.promotion.domain.model.Coupon;
import com.platform.promotion.domain.model.CouponUsage;
import com.platform.promotion.domain.repository.CouponRepository;
import com.platform.promotion.domain.repository.CouponUsageRepository;
import com.platform.promotion.domain.service.CouponValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final CouponValidationService validationService;

    /**
     * Calculate discount amount for a given coupon code and order subtotal.
     *
     * @param couponCode the coupon code
     * @param subtotal   order subtotal before discount
     * @param customerId customer ID (for per‑customer limit checks)
     * @return discount amount (zero if coupon invalid or expired)
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscount(String couponCode, BigDecimal subtotal, UUID customerId) {
        if (couponCode == null || couponCode.isBlank()) {
            return BigDecimal.ZERO;
        }

        Optional<Coupon> couponOpt = couponRepository.findByCodeAndActiveTrue(couponCode);
        if (couponOpt.isEmpty()) {
            log.debug("Coupon not found or inactive: {}", couponCode);
            return BigDecimal.ZERO;
        }

        Coupon coupon = couponOpt.get();
        if (!validationService.isValid(coupon, subtotal, customerId)) {
            log.debug("Coupon validation failed: {}", couponCode);
            return BigDecimal.ZERO;
        }

        return coupon.calculateDiscount(subtotal);
    }

    /**
     * Apply a coupon usage (record usage and increment counter).
     * Called after order is placed.
     */
    @Transactional
    public void applyCoupon(String couponCode, UUID customerId, UUID orderId) {
        Coupon coupon = couponRepository.findByCodeAndActiveTrue(couponCode)
                .orElseThrow(() -> new BusinessException("COUPON_NOT_FOUND", "Coupon not found"));

        // Increment usage count
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        // Record usage
        CouponUsage usage = CouponUsage.builder()
                .couponId(coupon.getId())
                .customerId(customerId)
                .orderId(orderId)
                .usedAt(Instant.now())
                .build();
        couponUsageRepository.save(usage);

        log.info("Coupon {} applied to order {} by customer {}", couponCode, orderId, customerId);
    }

    /**
     * Validate a coupon without applying it (useful for cart preview).
     */
    @Transactional(readOnly = true)
    public boolean validateCoupon(String couponCode, BigDecimal subtotal, UUID customerId) {
        if (couponCode == null || couponCode.isBlank()) return false;
        Optional<Coupon> couponOpt = couponRepository.findByCodeAndActiveTrue(couponCode);
        return couponOpt.isPresent() && validationService.isValid(couponOpt.get(), subtotal, customerId);
    }
}