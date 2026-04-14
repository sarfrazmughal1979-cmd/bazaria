package com.platform.cms.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cms_faq_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQCategory extends AuditableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "is_visible")
    private boolean visible;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<FAQ> faqs = new ArrayList<>();
}