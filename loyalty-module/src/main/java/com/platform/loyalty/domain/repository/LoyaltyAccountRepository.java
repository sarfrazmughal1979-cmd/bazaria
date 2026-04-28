package com.platform.loyalty.domain.repository;

import com.platform.loyalty.domain.model.LoyaltyAccount;
import com.platform.core.repository.BaseRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoyaltyAccountRepository extends BaseRepository<LoyaltyAccount> {
    Optional<LoyaltyAccount> findByCustomerId(UUID customerId);
}