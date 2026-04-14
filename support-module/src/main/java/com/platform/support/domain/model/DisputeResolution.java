package com.platform.support.domain.model;

import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "support_dispute_resolutions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisputeResolution extends BaseEntity {

    @Column(name = "dispute_id", nullable = false)
    private UUID disputeId;

    @Column(name = "action", nullable = false)
    private String action;  // ADMIN_NOTES, CUSTOMER_RESPONSE, VENDOR_RESPONSE, OFFER_MADE, OFFER_ACCEPTED

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "proposed_amount", precision = 19, scale = 4)
    private java.math.BigDecimal proposedAmount;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "actor_type")
    private String actorType;  // CUSTOMER, VENDOR, ADMIN
}