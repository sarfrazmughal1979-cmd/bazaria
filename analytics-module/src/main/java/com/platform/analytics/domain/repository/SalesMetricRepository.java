package com.platform.analytics.domain.repository;

import com.platform.analytics.domain.model.SalesMetric;
import com.platform.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalesMetricRepository extends BaseRepository<SalesMetric> {

    Optional<SalesMetric> findByMetricDateAndPeriodTypeAndVendorId(
        LocalDate date, String periodType, UUID vendorId);

    List<SalesMetric> findByVendorIdAndMetricDateBetweenOrderByMetricDateAsc(
        UUID vendorId, LocalDate start, LocalDate end);

    @Query("SELECT SUM(s.totalRevenue) FROM SalesMetric s WHERE s.metricDate BETWEEN :start AND :end AND s.vendorId IS NULL")
    BigDecimal sumPlatformRevenue(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT s FROM SalesMetric s WHERE s.periodType = 'DAY' AND s.metricDate >= :start ORDER BY s.metricDate DESC")
    List<SalesMetric> findLastNDays(@Param("start") LocalDate start);
}