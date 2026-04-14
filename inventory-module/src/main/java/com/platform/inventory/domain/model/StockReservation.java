package com.platform.inventory.domain.model;

import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_reservations", indexes = {
        @Index(name = "idx_reservation_inventory", columnList = "inventory_item_id"),
        @Index(name = "idx_reservation_order", columnList = "order_id"),
        @Index(name = "idx_reservation_status_expires", columnList = "status, expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReservation extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";   // ACTIVE, CONFIRMED, RELEASED, EXPIRED

    @Column(name = "order_id")
    private UUID orderId;               // associated order, if any

    @Column(name = "cart_id")
    private UUID cartId;                // optional – for cart reservations

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "released_at")
    private Instant releasedAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    // Domain methods

    /**
     * Check if the reservation is still valid (active and not expired).
     */
    public boolean isValid() {
        return "ACTIVE".equals(status) && Instant.now().isBefore(expiresAt);
    }

    /**
     * Confirm the reservation (e.g., order paid).
     */
    public void confirm() {
        if (!isValid()) {
            throw new IllegalStateException("Cannot confirm an invalid or expired reservation");
        }
        this.status = "CONFIRMED";
        this.confirmedAt = Instant.now();
    }

    /**
     * Release the reservation (e.g., order cancelled, cart abandoned).
     */
    public void release() {
        if ("RELEASED".equals(status) || "EXPIRED".equals(status)) {
            return; // already released
        }
        this.status = "RELEASED";
        this.releasedAt = Instant.now();
    }

    /**
     * Mark as expired (called by scheduled job).
     */
    public void expire() {
        if ("ACTIVE".equals(status) && Instant.now().isAfter(expiresAt)) {
            this.status = "EXPIRED";
            this.releasedAt = Instant.now();
        }
    }
}