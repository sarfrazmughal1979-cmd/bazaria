package com.platform.cms.application.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomepageSectionResponse {

    private UUID id;
    private String title;
    private String sectionType;
    private String sectionKey;
    private String subtitle;
    private String backgroundColor;
    private String textColor;
    private Integer sortOrder;
    private Boolean visible;
    private Integer maxItems;
    private String layout;
    private String configuration;
    private String deviceVisibility;
    private Instant startDate;
    private Instant endDate;
    private List<HomepageSectionItemResponse> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomepageSectionItemResponse {
        private UUID id;
        private String itemType;
        private String itemId;
        private String customTitle;
        private String customImageUrl;
        private String customLinkUrl;
        private Integer itemOrder;

        // Enriched fields (not persisted)
        private String enrichedTitle;
        private String enrichedImageUrl;
        private String enrichedLinkUrl;
    }
}