package com.platform.cms.domain.repository;

import com.platform.cms.domain.model.FAQ;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FAQRepository extends SoftDeleteRepository<FAQ> {

    List<FAQ> findByCategoryIdAndVisibleTrueOrderBySortOrderAsc(UUID categoryId);

    Page<FAQ> findByVisibleTrueAndDeletedFalse(Pageable pageable);

    @Query("SELECT f FROM FAQ f WHERE f.visible = true AND f.deleted = false " +
           "AND (LOWER(f.question) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(f.answer) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<FAQ> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE FAQ f SET f.helpfulCount = f.helpfulCount + 1 WHERE f.id = :id")
    void incrementHelpful(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE FAQ f SET f.notHelpfulCount = f.notHelpfulCount + 1 WHERE f.id = :id")
    void incrementNotHelpful(@Param("id") UUID id);
}