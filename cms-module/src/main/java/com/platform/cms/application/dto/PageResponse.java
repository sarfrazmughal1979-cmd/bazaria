package com.platform.cms.application.dto;

import com.platform.cms.domain.model.SEOMetadata;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse {
    private UUID id;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private String featuredImage;
    private String status;
    private SEOMetadata seoMetadata;
    private boolean showInFooter;
    private boolean showInHeader;
    private boolean isPublished;
    private Integer footerColumn;
    private Integer footerOrder;
    private long viewCount;
    private Instant publishedAt;
    private Instant createdAt;
    private Instant updatedAt;
}