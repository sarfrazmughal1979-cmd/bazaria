package com.platform.cms.domain.repository;

import com.platform.cms.domain.model.ContentBlock;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentBlockRepository extends SoftDeleteRepository<ContentBlock> {

    Optional<ContentBlock> findByBlockKeyAndActiveTrueAndDeletedFalse(String blockKey);
}