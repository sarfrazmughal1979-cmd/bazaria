package com.platform.catalog.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "brands", indexes = {
        @Index(name = "idx_brand_slug", columnList = "slug", unique = true),
        @Index(name = "idx_brand_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends AuditableEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "slug", unique = true, nullable = false, length = 255)
    private String slug;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;
}