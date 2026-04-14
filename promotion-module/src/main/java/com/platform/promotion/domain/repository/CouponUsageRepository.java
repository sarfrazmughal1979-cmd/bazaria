package com.platform.promotion.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.promotion.domain.model.CouponUsage;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CouponUsageRepository extends BaseRepository<CouponUsage> {
    long countByCouponIdAndCustomerId(UUID couponId, UUID customerId);
}