package com.platform.order.domain.model;

import com.platform.core.domain.Address;
import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.Money;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_customer", columnList = "customer_id"),
    @Index(name = "idx_order_number", columnList = "order_number", unique = true),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends AuditableEntity {

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

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
        @AttributeOverride(name = "amount", column = @Column(name = "discount_amount")),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "discount_currency"))
    })
    private Money discountAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "tax_amount")),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "tax_currency"))
    })
    private Money taxAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount")),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "total_currency"))
    })
    private Money totalAmount;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "addressLine1", column = @Column(name = "shipping_address_line1", insertable=false, updatable=false)),
        @AttributeOverride(name = "addressLine2", column = @Column(name = "shipping_address_line2", insertable=false, updatable=false)),
        @AttributeOverride(name = "city", column = @Column(name = "shipping_city", insertable=false, updatable=false)),
        @AttributeOverride(name = "state", column = @Column(name = "shipping_state", insertable=false, updatable=false)),
        @AttributeOverride(name = "postalCode", column = @Column(name = "shipping_postal_code", insertable=false, updatable=false)),
        @AttributeOverride(name = "country", column = @Column(name = "shipping_country", insertable=false, updatable=false)),
//        @AttributeOverride(name = "latitude", column = @Column(name = "shipping_latitude", insertable=false, updatable=false)),
        @AttributeOverride(name = "longitude", column = @Column(name = "shipping_longitude", insertable=false, updatable=false))
    })
    private Address shippingAddress;

    @Column(name = "customer_note")
    private String customerNote;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubOrder> subOrders = new ArrayList<>();
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<OrderTimeline> timeline = new ArrayList<>();

    // Domain methods
    public void addSubOrder(SubOrder subOrder) {
        subOrders.add(subOrder);
        subOrder.setOrder(this);
    }
    public void addTimelineEntry(String status, String description) {
        OrderTimeline entry = OrderTimeline.builder()
                .order(this)
                .status(status)
                .description(description)
                .build();
        timeline.add(entry);
    }

    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot confirm order in status: " + status);
        }
        this.status = OrderStatus.CONFIRMED;
        addTimelineEntry("CONFIRMED", "Order has been confirmed");
    }

    public void cancel(String reason) {
        if (this.status == OrderStatus.DELIVERED || this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order in status: " + status);
        }
        this.status = OrderStatus.CANCELLED;
        addTimelineEntry("CANCELLED", "Order cancelled: " + reason);
    }

    public static String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}