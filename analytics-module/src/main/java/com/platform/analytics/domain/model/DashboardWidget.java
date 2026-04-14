package com.platform.analytics.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "dashboard_widgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardWidget extends AuditableEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;           // admin or vendor

    @Column(name = "widget_type", nullable = false)
    private String widgetType;     // SALES_CHART, TOP_PRODUCTS, REVENUE_METER

    @Column(name = "title")
    private String title;

    @Column(name = "position_x")
    private int positionX;

    @Column(name = "position_y")
    private int positionY;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    @Column(name = "configuration", columnDefinition = "TEXT")
    private String configuration;  // JSON config
}