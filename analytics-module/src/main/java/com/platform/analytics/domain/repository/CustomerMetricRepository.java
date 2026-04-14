package com.platform.analytics.domain.repository;

import com.platform.analytics.domain.model.CustomerMetric;
import com.platform.core.repository.BaseRepository;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerMetricRepository extends SoftDeleteRepository<CustomerMetric> {

    @Query("SELECT COUNT(DISTINCT c.customerId) FROM CustomerMetric c " +
           "WHERE c.metricDate BETWEEN :start AND :end AND c.totalOrders > 0")
    long countActiveCustomers(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT AVG(c.totalSpent) FROM CustomerMetric c WHERE c.metricDate = :date")
    Double getAverageCustomerLifetimeValue(@Param("date") LocalDate date);
}