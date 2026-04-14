package com.platform.support.domain.model;

import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.Money;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "support_disputes", indexes = {
    @Index(name = "idx_dispute_order", columnList = "order_id"),
    @Index(name = "idx_dispute_suborder", columnList = "sub_order_id"),
    @Index(name = "idx_dispute_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispute extends AuditableEntity {

    @Column(name = "dispute_number", unique = true, nullable = false)
    private String disputeNumber;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "sub_order_id")
    private UUID subOrderId;  // specific vendor sub-order

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;

    @Column(name = "reason", nullable = false)
    private String reason;  // DAMAGED, WRONG_ITEM, NOT_RECEIVED, QUALITY_ISSUE, OTHER

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "disputed_amount")),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "dispute_currency"))
    })
    private Money disputedAmount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DisputeStatus status;  // PENDING, UNDER_REVIEW, RESOLVED_CUSTOMER, RESOLVED_VENDOR, CLOSED

    @Column(name = "resolution")
    private String resolution;  // REFUND, REPLACEMENT, PARTIAL_REFUND, DISMISSED

    @Column(name = "resolution_amount", precision = 19, scale = 4)
    private java.math.BigDecimal resolutionAmount;

    @Column(name = "resolved_by")
    private UUID resolvedBy;  // admin ID

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "evidence_urls")
    private String evidenceUrls;  // JSON array

    @Column(name = "admin_notes")
    private String adminNotes;

    public void resolve(String resolution, java.math.BigDecimal amount, UUID adminId) {
        this.resolution = resolution;
        this.resolutionAmount = amount;
        this.resolvedBy = adminId;
        this.resolvedAt = Instant.now();
        this.status = resolution.startsWith("REFUND") ? DisputeStatus.RESOLVED_CUSTOMER : DisputeStatus.RESOLVED;
    }

    public static String generateDisputeNumber() {
        return "DSP-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}