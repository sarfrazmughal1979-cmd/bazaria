package com.platform.cms.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "cms_announcements", indexes = {
    @Index(name = "idx_announcement_dates", columnList = "start_date, end_date"),
    @Index(name = "idx_announcement_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement extends AuditableEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "type", nullable = false)
    private String type;  // INFO, SUCCESS, WARNING, DANGER, PROMOTION

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "link_text")
    private String linkText;

    @Column(name = "icon")
    private String icon;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "is_dismissible")
    private boolean dismissible;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "target_pages")
    private String targetPages;  // ALL, HOMEPAGE_ONLY, CHECKOUT_PAGE, comma-separated

    @Column(name = "priority")
    private int priority;  // higher = more important

    public boolean isCurrentlyActive() {
        Instant now = Instant.now();
        return active && (startDate == null || now.isAfter(startDate)) &&
               (endDate == null || now.isBefore(endDate));
    }
}