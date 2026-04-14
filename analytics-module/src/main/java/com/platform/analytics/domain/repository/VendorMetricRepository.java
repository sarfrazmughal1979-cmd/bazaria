package com.platform.analytics.domain.repository;

import com.platform.analytics.domain.model.VendorMetric;
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
public interface VendorMetricRepository extends BaseRepository<VendorMetric> {

    Optional<VendorMetric> findByVendorIdAndMetricDate(UUID vendorId, LocalDate date);

    List<VendorMetric> findByVendorIdAndMetricDateBetweenOrderByMetricDateAsc(
        UUID vendorId, LocalDate start, LocalDate end);

    @Query("SELECT v.vendorId, SUM(v.totalRevenue) as revenue FROM VendorMetric v " +
           "WHERE v.metricDate BETWEEN :start AND :end GROUP BY v.vendorId ORDER BY revenue DESC")
    Page<Object[]> findTopVendorsByRevenue(@Param("start") LocalDate start,
                                            @Param("end") LocalDate end,
                                            Pageable pageable);
}