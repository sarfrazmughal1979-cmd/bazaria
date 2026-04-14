package com.platform.cms.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cms_banners", indexes = {
    @Index(name = "idx_banner_position", columnList = "position"),
    @Index(name = "idx_banner_active_dates", columnList = "is_active, start_date, end_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner extends AuditableEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "mobile_image_url")
    private String mobileImageUrl;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "link_type")
    private String linkType;  // PRODUCT, CATEGORY, PAGE, EXTERNAL, VENDOR

    @Column(name = "link_value")
    private String linkValue;  // product slug, category ID, URL

    @Column(name = "position", nullable = false)
    private String position;   // HOMEPAGE_TOP, HOMEPAGE_MIDDLE, HOMEPAGE_BOTTOM, CATEGORY_PAGE

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "target_audience")
    private String targetAudience;  // ALL, NEW_CUSTOMERS, RETURNING_CUSTOMERS, GUESTS

    @Column(name = "click_count")
    private long clickCount;

    @Column(name = "impression_count")
    private long impressionCount;

    public void incrementClick() {
        this.clickCount++;
    }

    public void incrementImpression() {
        this.impressionCount++;
    }

    public boolean isCurrentlyActive() {
        Instant now = Instant.now();
        return active && (startDate == null || now.isAfter(startDate)) &&
               (endDate == null || now.isBefore(endDate));
    }
}