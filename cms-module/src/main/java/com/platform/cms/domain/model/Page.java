package com.platform.cms.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "cms_pages", indexes = {
    @Index(name = "idx_page_slug", columnList = "slug", unique = true),
    @Index(name = "idx_page_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Page extends AuditableEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;  // HTML content

    @Column(name = "excerpt", length = 500)
    private String excerpt;

    @Column(name = "featured_image")
    private String featuredImage;

    @Column(name = "status", nullable = false)
    private String status;  // DRAFT, PUBLISHED, ARCHIVED

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "author_id")
    private String authorId;

    @Column(name = "view_count")
    private long viewCount;

    @Embedded
    private SEOMetadata seoMetadata;

    @Column(name = "show_in_footer")
    private boolean showInFooter;

    @Column(name = "show_in_header")
    private boolean showInHeader;

    @Column(name = "footer_column")
    private Integer footerColumn;  // 1, 2, 3, 4

    @Column(name = "footer_order")
    private Integer footerOrder;

    @Column(name = "is_published")
    private boolean isPublished;

    public void publish() {
        this.status = "PUBLISHED";
        this.publishedAt = Instant.now();
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}