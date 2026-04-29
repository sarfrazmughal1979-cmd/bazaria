package com.platform.loyalty.domain.repository;

import com.platform.loyalty.domain.model.LoyaltyTransaction;
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
public interface LoyaltyTransactionRepository extends BaseRepository<LoyaltyTransaction> {
    Page<LoyaltyTransaction> findByCustomerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);
    @Query("SELECT SUM(t.points) FROM LoyaltyTransaction t WHERE t.customerId = :customerId AND t.type = :type AND t.createdAt >= :after")
    Optional<Long> sumPointsByCustomerAndTypeAndAfter(
            @Param("customerId") UUID customerId,
            @Param("type") String type,     // "EARN" or "REDEEM"
            @Param("after") Instant after);
}