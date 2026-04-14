package com.platform.catalog.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SEOMetadata {

    @Column(name = "seo_title", length = 255)
    private String title;

    @Column(name = "seo_description", length = 500)
    private String description;

    @Column(name = "seo_keywords", length = 500)
    private String keywords;

    @Column(name = "seo_canonical_url", length = 500)
    private String canonicalUrl;

    @Column(name = "seo_robots", length = 100)
    private String robots;  // e.g., "index,follow"
}