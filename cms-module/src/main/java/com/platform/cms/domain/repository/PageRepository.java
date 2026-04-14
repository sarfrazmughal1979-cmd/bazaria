package com.platform.cms.domain.repository;

import com.platform.cms.domain.model.Page;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends SoftDeleteRepository<Page> {

    Optional<com.platform.cms.domain.model.Page> findBySlugAndStatus(String slug, String status);

    Optional<com.platform.cms.domain.model.Page> findBySlugAndDeletedFalse(String slug);

    org.springframework.data.domain.Page<Page> findByStatusAndDeletedFalse(String status, Pageable pageable);

    org.springframework.data.domain.Page<Page> findByShowInFooterTrueAndDeletedFalseOrderByFooterOrderAsc(Pageable pageable);

    org.springframework.data.domain.Page<Page> findByShowInHeaderTrueAndDeletedFalseOrderByFooterOrderAsc(Pageable pageable);

    boolean existsBySlugAndDeletedFalse(String slug);
}