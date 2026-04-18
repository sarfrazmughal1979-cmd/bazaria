package com.platform.order.domain.model;

import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.Money;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sub_orders", indexes = {
    @Index(name = "idx_suborder_vendor", columnList = "vendor_id"),
    @Index(name = "idx_suborder_order", columnList = "order_id"),
    @Index(name = "idx_suborder_number", columnList = "sub_order_number", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubOrder extends AuditableEntity {

    @Column(name = "sub_order_number", unique = true, nullable = false)
    private String subOrderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubOrderStatus status;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name= "delivered_at")
    private Instant deliveredAt;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "subtotal")),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "subtotal_currency"))
    })
    private Money subtotal;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "shipping_cost")),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "shipping_currency"))
    })
    private Money shippingCost;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "commission_amount")),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "commission_currency"))
    })
    private Money commissionAmount;

    @Column(name = "shipment_id")
    private UUID shipmentId;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @OneToMany(mappedBy = "subOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    // ============================================================
    // Explicit getter for items (in case Lombok fails)
    // ============================================================
    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setSubOrder(this);
    }

    public void markAsShipped(String trackingNumber) {
        this.status = SubOrderStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
    }

    public void markAsDelivered() {
        this.status = SubOrderStatus.DELIVERED;
    }
}