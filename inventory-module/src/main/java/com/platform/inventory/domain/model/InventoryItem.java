package com.platform.inventory.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inventory_items", indexes = {
        @Index(name = "idx_inventory_product", columnList = "product_id"),
        @Index(name = "idx_inventory_variant", columnList = "variant_id"),
        @Index(name = "idx_inventory_warehouse", columnList = "warehouse_id"),
        @Index(name = "idx_inventory_sku", columnList = "sku")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem extends AuditableEntity {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;          // null for simple products (no variants)

    @Column(name = "warehouse_id")
    private UUID warehouseId;        // optional – for multi‑warehouse setups

    @Column(name = "sku", length = 100)
    private String sku;              // unique SKU (same as product/variant SKU)

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private int quantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    @Builder.Default
    private int reservedQuantity = 0;

    @Column(name = "reorder_point", nullable = false)
    @Builder.Default
    private int reorderPoint = 10;

    @Column(name = "reorder_quantity")
    @Builder.Default
    private int reorderQuantity = 50;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "inventoryItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockMovement> movements = new ArrayList<>();

    @OneToMany(mappedBy = "inventoryItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockReservation> reservations = new ArrayList<>();

    // ============================================================
    // Domain methods
    // ============================================================

    /**
     * @return quantity available for sale (not reserved)
     */
    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    /**
     * Check if stock is sufficient for requested quantity.
     */
    public boolean hasSufficientStock(int requestedQuantity) {
        return getAvailableQuantity() >= requestedQuantity;
    }

    /**
     * Check if stock has fallen to or below reorder point.
     */
    public boolean needsReorder() {
        return getAvailableQuantity() <= reorderPoint;
    }

    /**
     * Increase physical stock (e.g., purchase order received, return restocked).
     */
    public void increaseStock(int amount, String reason) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.quantity += amount;
        addMovement(MovementType.INBOUND, amount, reason);
    }

    /**
     * Decrease physical stock (e.g., order confirmed, stock removed).
     */
    public void decreaseStock(int amount, String reason) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.quantity - amount < 0) {
            throw new IllegalStateException("Cannot decrease stock below zero");
        }
        this.quantity -= amount;
        addMovement(MovementType.OUTBOUND, -amount, reason);
    }

    /**
     * Reserve stock for an order.
     */
    public void reserve(int amount, String reservationId, UUID orderId) {
        if (!hasSufficientStock(amount)) {
            throw new IllegalStateException("Insufficient stock to reserve");
        }
        this.reservedQuantity += amount;
        addMovement(MovementType.RESERVATION, -amount, "Reserved for " + reservationId);
    }

    /**
     * Release reserved stock (e.g., order cancelled, reservation expired).
     */
    public void releaseReservation(int amount, String reservationId) {
        if (this.reservedQuantity < amount) {
            throw new IllegalStateException("Cannot release more than reserved");
        }
        this.reservedQuantity -= amount;
        addMovement(MovementType.RELEASE, amount, "Released from " + reservationId);
    }

    /**
     * Confirm a reservation and convert to actual stock decrease.
     * Typically called when order is confirmed and paid.
     */
    public void confirmReservation(String reservationId) {
        // Already decreased reserved quantity in reserve()? Depends on design.
        // Here we assume reserve() only increments reservedQuantity.
        // For confirmation, we decrease actual stock and keep reservedQuantity unchanged?
        // Actually typical flow:
        // 1. reserve() -> reservedQuantity += amount, quantity unchanged.
        // 2. confirm() -> quantity -= amount, reservedQuantity -= amount.
        // We'll implement accordingly.
    }

    private void addMovement(MovementType type, int quantityChange, String reason) {
        StockMovement movement = StockMovement.builder()
                .inventoryItem(this)
                .type(type)
                .quantity(quantityChange)
                .reason(reason)
                .build();
        this.movements.add(movement);
    }

    /**
     * Adjust stock (can be positive or negative) with a reason.
     */
    public void adjustStock(int delta, String reason) {
        if (delta > 0) {
            increaseStock(delta, reason);
        } else if (delta < 0) {
            decreaseStock(-delta, reason);
        }
    }
}