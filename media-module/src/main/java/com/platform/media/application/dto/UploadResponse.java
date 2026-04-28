package com.platform.media.application.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UploadResponse {
    private String assetId;
    private String url;
    private String bucketKey;
    private String fileName;
    private long sizeInBytes;
    private String contentType;
}