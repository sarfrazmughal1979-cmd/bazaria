package com.platform.media.domain.repository;

import com.platform.media.domain.model.MediaAsset;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaAssetRepository extends SoftDeleteRepository<MediaAsset> {

    Optional<MediaAsset> findByBucketKey(String bucketKey);

    Page<MediaAsset> findByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);
}