package com.platform.support.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "support_tickets", indexes = {
    @Index(name = "idx_ticket_number", columnList = "ticket_number", unique = true),
    @Index(name = "idx_ticket_customer", columnList = "customer_id"),
    @Index(name = "idx_ticket_vendor", columnList = "vendor_id"),
    @Index(name = "idx_ticket_status", columnList = "status"),
    @Index(name = "idx_ticket_assigned", columnList = "assigned_to")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket extends AuditableEntity {

    @Column(name = "ticket_number", unique = true, nullable = false)
    private String ticketNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "vendor_id")
    private UUID vendorId;  // null if ticket is about platform, not specific vendor

    @Column(name = "order_id")
    private UUID orderId;   // optional link to order

    @ManyToOne
    @JoinColumn(name = "category_id")
    private TicketCategory category;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TicketPriority priority;

    @Column(name = "assigned_to")
    private UUID assignedTo;  // support agent ID

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "first_response_at")
    private Instant firstResponseAt;

    @Column(name = "last_response_at")
    private Instant lastResponseAt;

    @Column(name = "customer_rating")
    private Integer customerRating;  // 1-5

    @Column(name = "customer_feedback")
    private String customerFeedback;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<TicketMessage> messages = new ArrayList<>();

    // Domain methods
    public void assignTo(UUID agentId) {
        this.assignedTo = agentId;
        this.assignedAt = Instant.now();
        this.status = TicketStatus.IN_PROGRESS;
    }

    public void escalate() {
        this.status = TicketStatus.ESCALATED;
        this.priority = TicketPriority.URGENT;
    }

    public void resolve() {
        this.status = TicketStatus.RESOLVED;
        this.resolvedAt = Instant.now();
    }

    public void close() {
        this.status = TicketStatus.CLOSED;
        this.closedAt = Instant.now();
    }

    public void reopen() {
        this.status = TicketStatus.OPEN;
        this.resolvedAt = null;
        this.closedAt = null;
    }

    public void addMessage(TicketMessage message) {
        messages.add(message);
        message.setTicket(this);
        this.lastResponseAt = Instant.now();
    }

    public void updateFirstResponse() {
        if (this.firstResponseAt == null && messages.size() > 0) {
            this.firstResponseAt = messages.get(0).getCreatedAt();
        }
    }

    public static String generateTicketNumber() {
        return "TKT-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}