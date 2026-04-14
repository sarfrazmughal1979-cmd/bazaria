package com.platform.support.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "support_ticket_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "sender_type", nullable = false)
    private String senderType;  // CUSTOMER, VENDOR, AGENT, SYSTEM

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "is_internal")
    private boolean internal;  // internal notes (not visible to customer/vendor)

    @Column(name = "attachments")
    private String attachments;  // JSON array of file URLs

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "created_at")
    private Instant createdAt;

    public void markAsRead() {
        this.readAt = Instant.now();
    }
}