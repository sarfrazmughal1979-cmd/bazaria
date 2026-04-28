package com.platform.pricing.domain.repository;

import com.platform.pricing.domain.model.TaxRule;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaxRuleRepository extends SoftDeleteRepository<TaxRule> {

    @Query("SELECT t FROM TaxRule t WHERE t.active = true AND t.countryCode = :countryCode " +
           "AND (t.stateCode = :stateCode OR t.stateCode IS NULL) " +
           "AND (t.categoryId = :categoryId OR t.categoryId IS NULL) " +
           "ORDER BY t.priority ASC")
    List<TaxRule> findApplicableRules(@Param("countryCode") String countryCode,
                                      @Param("stateCode") String stateCode,
                                      @Param("categoryId") UUID categoryId);
}