package com.platform.settlement.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.settlement.domain.model.CommissionRule;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommissionRuleRepository extends BaseRepository<CommissionRule> {
    Optional<CommissionRule> findByVendorId(UUID vendorId);
    Optional<CommissionRule> findByCategoryId(UUID categoryId);
    List<CommissionRule> findByIsDefaultTrue();
}