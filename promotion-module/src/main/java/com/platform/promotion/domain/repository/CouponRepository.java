package com.platform.promotion.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.promotion.domain.model.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends BaseRepository<Coupon> {

    Optional<Coupon> findByCode(String code);

    Optional<Coupon> findByCodeAndActiveTrue(String code);

    Page<Coupon> findByVendorIdOrVendorIdIsNull(UUID vendorId, Pageable pageable);

    Page<Coupon> findByActiveTrue(Pageable pageable);

    @Query("SELECT c FROM Coupon c WHERE c.active = true AND c.startDate <= :now AND c.endDate >= :now")
    List<Coupon> findCurrentlyActive(@Param("now") Instant now);

    @Query("SELECT c FROM Coupon c WHERE c.vendorId = :vendorId AND c.active = true AND c.endDate >= :now")
    List<Coupon> findActiveByVendorId(@Param("vendorId") UUID vendorId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE Coupon c SET c.usedCount = c.usedCount + 1 WHERE c.id = :id")
    void incrementUsedCount(@Param("id") UUID id);

    boolean existsByCode(String code);
}