package com.platform.fraud.domain.repository;

import com.platform.fraud.domain.model.FraudRule;
import com.platform.core.repository.BaseRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FraudRuleRepository extends BaseRepository<FraudRule> {
    List<FraudRule> findByActiveTrue();
}