package com.platform.inventory.domain.model;

import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_movements", indexes = {
        @Index(name = "idx_stock_movement_inventory", columnList = "inventory_item_id"),
        @Index(name = "idx_stock_movement_type", columnList = "type"),
        @Index(name = "idx_stock_movement_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private MovementType type;

    @Column(name = "quantity", nullable = false)
    private int quantity;          // positive for inbound, negative for outbound

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "reference_id")
    private UUID referenceId;      // e.g., order ID, purchase order ID, return ID

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}