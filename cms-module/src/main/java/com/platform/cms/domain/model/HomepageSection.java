package com.platform.cms.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cms_homepage_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomepageSection extends AuditableEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "section_type", nullable = false)
    private String sectionType;  // FEATURED_PRODUCTS, CATEGORY_GRID, BANNER, FLASH_SALE, BRAND_STRIP, NEW_ARRIVALS

    @Column(name = "section_key")
    private String sectionKey;   // unique identifier for frontend reference

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "background_color")
    private String backgroundColor;

    @Column(name = "text_color")
    private String textColor;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "is_visible")
    private boolean visible;

    @Column(name = "max_items")
    private Integer maxItems;  // limit for product/category sections

    @Column(name = "layout")
    private String layout;  // GRID, LIST, CAROUSEL, SLIDER

    @Column(name = "configuration", columnDefinition = "TEXT")
    private String configuration;  // JSON config for dynamic sections

    @Column(name = "device_visibility")
    private String deviceVisibility;  // ALL, MOBILE_ONLY, DESKTOP_ONLY

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("itemOrder ASC")
    @Builder.Default
    private List<HomepageSectionItem> items = new ArrayList<>();

    public void addItem(HomepageSectionItem item) {
        items.add(item);
        item.setSection(this);
    }
}