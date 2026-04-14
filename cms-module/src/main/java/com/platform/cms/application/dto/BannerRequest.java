package com.platform.cms.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerRequest {

    @NotBlank
    private String title;

    private String subtitle;

    @NotBlank
    private String imageUrl;

    private String mobileImageUrl;

    private String linkUrl;

    private String linkType;  // PRODUCT, CATEGORY, PAGE, EXTERNAL

    private String linkValue;

    @NotBlank
    private String position;

    private Integer sortOrder;

    private Boolean active;

    private Instant startDate;

    private Instant endDate;

    private String targetAudience;
}