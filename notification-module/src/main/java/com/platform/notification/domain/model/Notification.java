package com.platform.notification.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_user", columnList = "user_id"),
        @Index(name = "idx_notification_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends AuditableEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "channel", length = 30)
    private String channel;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
}