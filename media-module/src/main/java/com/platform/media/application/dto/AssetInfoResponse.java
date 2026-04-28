package com.platform.media.application.dto;

import lombok.*;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssetInfoResponse {
    private String id;
    private String fileName;
    private String originalFileName;
    private String contentType;
    private long sizeInBytes;
    private String url;
    private Integer width;
    private Integer height;
    private String entityType;
    private String entityId;
    private Instant createdAt;
}