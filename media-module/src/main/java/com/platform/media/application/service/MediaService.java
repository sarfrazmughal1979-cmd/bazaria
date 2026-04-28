package com.platform.media.application.service;

import com.platform.core.cloud.storage.StorageService;
import com.platform.core.exception.BusinessException;
import com.platform.media.application.dto.AssetInfoResponse;
import com.platform.media.application.dto.UploadResponse;
import com.platform.media.application.mapper.MediaMapper;
import com.platform.media.domain.model.MediaAsset;
import com.platform.media.domain.repository.MediaAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaAssetRepository mediaAssetRepository;
    private final StorageService storageService;
    private final MediaMapper mapper;

    @Transactional
    public UploadResponse upload(MultipartFile file, String folder, String entityType, UUID entityId) {
        // 1. Upload to S3
        String fileUrl = storageService.upload(file, folder);

        // 2. Extract bucket key from URL (assuming standard S3 URL format)
        String bucketKey = extractKey(fileUrl);

        // 3. Save metadata
        MediaAsset asset = MediaAsset.builder()
                .fileName(file.getName())
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .sizeInBytes(file.getSize())
                .bucketKey(bucketKey)
                .url(fileUrl)
                .entityType(entityType)
                .entityId(entityId)
                .build();
        asset = mediaAssetRepository.save(asset);

        return mapper.toUploadResponse(asset);
    }

    @Transactional(readOnly = true)
    public AssetInfoResponse getAsset(UUID assetId) {
        MediaAsset asset = mediaAssetRepository.findById(assetId)
                .orElseThrow(() -> new BusinessException("ASSET_NOT_FOUND", "Media asset not found"));
        return mapper.toAssetInfo(asset);
    }

    @Transactional
    public void delete(UUID assetId) {
        MediaAsset asset = mediaAssetRepository.findById(assetId)
                .orElseThrow(() -> new BusinessException("ASSET_NOT_FOUND", "Media asset not found"));
        storageService.delete(asset.getUrl());
        mediaAssetRepository.softDelete(assetId);
    }

    public String generatePresignedUrl(UUID assetId, int expirationMinutes) {
        MediaAsset asset = mediaAssetRepository.findById(assetId)
                .orElseThrow(() -> new BusinessException("ASSET_NOT_FOUND", "Media asset not found"));
        return storageService.getPresignedUrl(asset.getBucketKey(), expirationMinutes);
    }

    private String extractKey(String fileUrl) {
        // Assuming URL is https://<bucket>.s3.<region>.amazonaws.com/<key>
        int index = fileUrl.indexOf(".com/");
        if (index >= 0) {
            return fileUrl.substring(index + 5);
        }
        return fileUrl;
    }
}