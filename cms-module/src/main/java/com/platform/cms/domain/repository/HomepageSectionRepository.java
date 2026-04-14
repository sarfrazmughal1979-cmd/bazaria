package com.platform.cms.domain.repository;

import com.platform.cms.domain.model.HomepageSection;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface HomepageSectionRepository extends SoftDeleteRepository<HomepageSection> {

    List<HomepageSection> findByVisibleTrueAndDeletedFalseOrderBySortOrderAsc();

    List<HomepageSection> findByVisibleTrueAndDeletedFalseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        Instant now, Instant now2);
}