package com.platform.support.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "support_chat_sessions", indexes = {
    @Index(name = "idx_chat_customer", columnList = "customer_id"),
    @Index(name = "idx_chat_vendor", columnList = "vendor_id"),
    @Index(name = "idx_chat_agent", columnList = "agent_id"),
    @Index(name = "idx_chat_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession extends AuditableEntity {

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "vendor_id")
    private UUID vendorId;

    @Column(name = "agent_id")
    private UUID agentId;  // support agent

    @Column(name = "ticket_id")
    private UUID ticketId;  // optional link to ticket

    @Column(name = "status", nullable = false)
    private String status;  // ACTIVE, CLOSED, TRANSFERRED

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "customer_rating")
    private Integer customerRating;

    @Column(name = "transcript")
    private String transcript;  // JSON transcript for storage

    public void close() {
        this.status = "CLOSED";
        this.endedAt = Instant.now();
    }
}