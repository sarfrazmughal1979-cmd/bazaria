package com.platform.cms.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cms_content_blocks", indexes = {
    @Index(name = "idx_content_block_key", columnList = "block_key", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentBlock extends AuditableEntity {

    @Column(name = "block_key", unique = true, nullable = false)
    private String blockKey;  // e.g., "homepage_hero_text", "footer_copyright"

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;  // HTML content

    @Column(name = "content_type")
    private String contentType;  // HTML, MARKDOWN, TEXT

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "cache_ttl_minutes")
    private Integer cacheTtlMinutes;  // override default cache TTL
}