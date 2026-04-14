package com.platform.cms.domain.model;
import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "cms_homepage_section_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomepageSectionItem extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private HomepageSection section;

    @Column(name = "item_type", nullable = false)
    private String itemType;  // PRODUCT, CATEGORY, BRAND, CUSTOM

    @Column(name = "item_id")
    private UUID itemId;      // product_id, category_id, brand_id

    @Column(name = "custom_title")
    private String customTitle;

    @Column(name = "custom_image_url")
    private String customImageUrl;

    @Column(name = "custom_link_url")
    private String customLinkUrl;

    @Column(name = "item_order")
    private int itemOrder;
}