package com.platform.cms.domain.repository;

import com.platform.cms.domain.model.FAQCategory;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FAQCategoryRepository extends SoftDeleteRepository<FAQCategory> {

    Optional<FAQCategory> findBySlugAndDeletedFalse(String slug);

    List<FAQCategory> findByVisibleTrueAndDeletedFalseOrderBySortOrderAsc();

    boolean existsBySlugAndDeletedFalse(String slug);
}