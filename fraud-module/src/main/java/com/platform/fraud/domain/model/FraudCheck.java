package com.platform.fraud.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fraud_checks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FraudCheck extends BaseEntity {

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "risk_score")
    private int riskScore;

    @Column(name = "status", length = 20, nullable = false)
    private String status;   // APPROVED, REVIEW, REJECTED

    @Column(name = "reasons", columnDefinition = "TEXT")
    private String reasons;  // JSON array of triggered rules

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;

    @PrePersist
    protected void onCreate() { checkedAt = Instant.now(); }
}