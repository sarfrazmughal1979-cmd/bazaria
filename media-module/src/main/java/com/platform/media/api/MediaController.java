package com.platform.media.api;

import com.platform.core.dto.ApiResponse;
import com.platform.media.application.dto.AssetInfoResponse;
import com.platform.media.application.dto.UploadResponse;
import com.platform.media.application.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a file")
    public ResponseEntity<ApiResponse<UploadResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "general") String folder,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) UUID entityId) {
        UploadResponse response = mediaService.upload(file, folder, entityType, entityId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{assetId}")
    @Operation(summary = "Get asset metadata")
    public ResponseEntity<ApiResponse<AssetInfoResponse>> getAsset(@PathVariable UUID assetId) {
        return ResponseEntity.ok(ApiResponse.success(mediaService.getAsset(assetId)));
    }

    @DeleteMapping("/{assetId}")
    @Operation(summary = "Delete an asset")
    public ResponseEntity<ApiResponse<Void>> deleteAsset(@PathVariable UUID assetId) {
        mediaService.delete(assetId);
        return ResponseEntity.ok(ApiResponse.success("Asset deleted"));
    }

    @GetMapping("/{assetId}/presigned-url")
    @Operation(summary = "Generate a presigned URL")
    public ResponseEntity<ApiResponse<String>> getPresignedUrl(
            @PathVariable UUID assetId,
            @RequestParam(defaultValue = "60") int expirationMinutes) {
        String url = mediaService.generatePresignedUrl(assetId, expirationMinutes);
        return ResponseEntity.ok(ApiResponse.success(url));
    }
}