package com.platform.notification.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate extends AuditableEntity {

    @Column(name = "template_key", unique = true, nullable = false)
    private String templateKey;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "channels", length = 255)
    private String channels; // comma-separated: "EMAIL,SMS"

    @Column(name = "is_active")
    private boolean active;
}