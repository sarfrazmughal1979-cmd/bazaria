package com.platform.loyalty.domain.repository;

import com.platform.loyalty.domain.model.LoyaltyTransaction;
import com.platform.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface LoyaltyTransactionRepository extends BaseRepository<LoyaltyTransaction> {
    Page<LoyaltyTransaction> findByCustomerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);
}