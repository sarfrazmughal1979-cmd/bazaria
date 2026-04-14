package com.platform.cms.application.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerResponse {
    private UUID id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private String mobileImageUrl;
    private String linkUrl;
    private String linkType;
    private String linkValue;
    private String position;
    private int sortOrder;
    private boolean active;
    private boolean currentlyActive;
    private Instant startDate;
    private Instant endDate;
    private String targetAudience;
    private long clickCount;
    private long impressionCount;
    private Instant createdAt;
    private Instant updatedAt;
}