package com.platform.analytics.domain.repository;

import com.platform.analytics.domain.model.ProductMetric;
import com.platform.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductMetricRepository extends BaseRepository<ProductMetric> {

    List<ProductMetric> findByProductIdAndMetricDateBetweenOrderByMetricDateAsc(
        UUID productId, LocalDate start, LocalDate end);

    @Query("SELECT p.productId, SUM(p.quantitySold) as sold FROM ProductMetric p " +
           "WHERE p.vendorId = :vendorId AND p.metricDate BETWEEN :start AND :end " +
           "GROUP BY p.productId ORDER BY sold DESC")
    Page<Object[]> findTopProductsByVendor(@Param("vendorId") UUID vendorId,
                                            @Param("start") LocalDate start,
                                            @Param("end") LocalDate end,
                                            Pageable pageable);

    List<ProductMetric> findByProductIdAndMetricDateBetween(UUID productId, LocalDate start, LocalDate end);

    // Also add this if needed for the collector:
    Optional<ProductMetric> findByProductIdAndMetricDate(UUID productId, LocalDate date);
}