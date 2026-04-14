package com.platform.cms.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomepageSectionRequest {

    @NotBlank
    private String title;

    @NotBlank
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

    private List<SectionItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionItemRequest {
        private String itemType;  // PRODUCT, CATEGORY, BRAND, CUSTOM
        private String itemId;
        private String customTitle;
        private String customImageUrl;
        private String customLinkUrl;
        private Integer itemOrder;
    }
}