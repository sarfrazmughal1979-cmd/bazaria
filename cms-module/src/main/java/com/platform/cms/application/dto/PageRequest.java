package com.platform.cms.application.dto;

import com.platform.cms.domain.model.SEOMetadata;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String slug;

    private String content;

    private String excerpt;

    private String featuredImage;

    private String status;  // DRAFT, PUBLISHED

    private SEOMetadata seoMetadata;

    private Boolean showInFooter;

    private Boolean showInHeader;

    private Boolean isPublished;

    private Integer footerColumn;

    private Integer footerOrder;
}