package com.platform.catalog.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_slug", columnList = "slug", unique = true),
    @Index(name = "idx_category_parent", columnList = "parent_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends AuditableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @Column(name = "level")
    private int level;

    @Column(name = "path")
    private String path; // e.g., "/electronics/phones/smartphones"

    @Column(name = "commission_rate")
    private java.math.BigDecimal commissionRate;
}