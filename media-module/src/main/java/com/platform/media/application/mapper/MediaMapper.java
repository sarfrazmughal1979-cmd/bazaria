package com.platform.media.application.mapper;

import com.platform.media.application.dto.AssetInfoResponse;
import com.platform.media.application.dto.UploadResponse;
import com.platform.media.domain.model.MediaAsset;
import org.springframework.stereotype.Component;

@Component
public class MediaMapper {

    public UploadResponse toUploadResponse(MediaAsset asset) {
        return UploadResponse.builder()
                .assetId(asset.getId().toString())
                .url(asset.getUrl())
                .bucketKey(asset.getBucketKey())
                .fileName(asset.getFileName())
                .sizeInBytes(asset.getSizeInBytes())
                .contentType(asset.getContentType())
                .build();
    }

    public AssetInfoResponse toAssetInfo(MediaAsset asset) {
        return AssetInfoResponse.builder()
                .id(asset.getId().toString())
                .fileName(asset.getFileName())
                .originalFileName(asset.getOriginalFileName())
                .contentType(asset.getContentType())
                .sizeInBytes(asset.getSizeInBytes())
                .url(asset.getUrl())
                .width(asset.getWidth())
                .height(asset.getHeight())
                .entityType(asset.getEntityType())
                .entityId(asset.getEntityId() != null ? asset.getEntityId().toString() : null)
                .createdAt(asset.getCreatedAt())
                .build();
    }
}