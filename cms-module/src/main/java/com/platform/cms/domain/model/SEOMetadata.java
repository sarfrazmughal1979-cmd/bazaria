package com.platform.cms.domain.model;

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

    @Column(name = "meta_title", length = 70)
    private String metaTitle;

    @Column(name = "meta_description", length = 160)
    private String metaDescription;

    @Column(name = "meta_keywords", length = 255)
    private String metaKeywords;

    @Column(name = "canonical_url")
    private String canonicalUrl;

    @Column(name = "og_title")
    private String ogTitle;

    @Column(name = "og_description")
    private String ogDescription;

    @Column(name = "og_image")
    private String ogImage;

    @Column(name = "schema_markup", columnDefinition = "TEXT")
    private String schemaMarkup;  // JSON-LD
}