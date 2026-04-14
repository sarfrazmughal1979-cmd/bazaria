package com.platform.promotion.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.promotion.domain.model.FlashSale;
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
public interface FlashSaleRepository extends BaseRepository<FlashSale> {

    Optional<FlashSale> findBySlug(String slug);

    Page<FlashSale> findByIsActiveTrue(Pageable pageable);

    Page<FlashSale> findByIsActiveFalse(Pageable pageable);

    List<FlashSale> findByIsActiveFalseAndStartTimeLessThanEqual(Instant startTime);

    List<FlashSale> findByIsActiveTrueAndEndTimeLessThanEqual(Instant endTime);

    // ========== Custom JPQL queries (alternative/backup) ==========
    @Query("SELECT fs FROM FlashSale fs WHERE fs.isActive = false AND fs.startTime <= :now")
    List<FlashSale> findPendingToActivate(@Param("now") Instant now);

    @Query("SELECT fs FROM FlashSale fs WHERE fs.isActive = true AND fs.endTime <= :now")
    List<FlashSale> findExpiredActiveSales(@Param("now") Instant now);

    @Query("SELECT fs FROM FlashSale fs WHERE fs.isActive = true AND fs.startTime <= :now AND fs.endTime >= :now")
    List<FlashSale> findCurrentlyActiveSales(@Param("now") Instant now);

    @Query("SELECT COUNT(fsi) > 0 FROM FlashSaleItem fsi WHERE fsi.flashSale.id = :flashSaleId AND fsi.productId = :productId")
    boolean existsByFlashSaleIdAndProductId(@Param("flashSaleId") UUID flashSaleId,
                                            @Param("productId") UUID productId);

    @Modifying
    @Query("UPDATE FlashSale fs SET fs.isActive = :active WHERE fs.id = :id")
    void updateActiveStatus(@Param("id") UUID id, @Param("active") boolean active);
}