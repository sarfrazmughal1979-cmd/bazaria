package com.platform.fraud.domain.repository;

import com.platform.fraud.domain.model.FraudCheck;
import com.platform.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FraudCheckRepository extends BaseRepository<FraudCheck> {
    Optional<FraudCheck> findByOrderId(UUID orderId);
    Page<FraudCheck> findByStatus(String status, Pageable pageable);

    @Query("SELECT COUNT(f) FROM FraudCheck f WHERE f.customerId = :customerId AND f.checkedAt > :since") long countByCustomerIdAndCheckedAtAfter(@Param("customerId") UUID customerId, @Param("since") Instant since);
}