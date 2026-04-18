package com.platform.cms.domain.repository;

import com.platform.cms.domain.model.Banner;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BannerRepository extends SoftDeleteRepository<Banner> {

    @Query("SELECT b FROM Banner b WHERE b.active = true AND " +
           "(b.startDate IS NULL OR b.startDate <= :now) AND " +
           "(b.endDate IS NULL OR b.endDate >= :now) " +
           "ORDER BY b.sortOrder ASC")
    List<Banner> findActiveBanners(@Param("now") Instant now);

    List<Banner> findByPositionAndActiveTrueOrderBySortOrderAsc(String position);

    @Query("SELECT b FROM Banner b WHERE b.position = :position AND b.active = true " +
           "AND (b.startDate IS NULL OR b.startDate <= :now) " +
           "AND (b.endDate IS NULL OR b.endDate >= :now) " +
           "ORDER BY b.sortOrder ASC")
    List<Banner> findActiveBannersByPosition(@Param("position") String position,
                                              @Param("now") Instant now);

    Page<Banner> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
}