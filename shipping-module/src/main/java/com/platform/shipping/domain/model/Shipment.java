package com.platform.shipping.domain.model;

import com.platform.core.domain.Address;
import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shipments", indexes = {
        @Index(name = "idx_shipment_suborder", columnList = "sub_order_id"),
        @Index(name = "idx_shipment_tracking", columnList = "tracking_number"),
        @Index(name = "idx_shipment_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends AuditableEntity {

    @Column(name = "sub_order_id", nullable = false)
    private UUID subOrderId;

    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "carrier")
    private String carrier;               // e.g., "DHL", "FedEx", "Courier"

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "method")
    private ShippingMethod method;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "addressLine1", column = @Column(name = "pickup_address_line1")),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "pickup_address_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "pickup_city")),
            @AttributeOverride(name = "state", column = @Column(name = "pickup_state")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "pickup_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "pickup_country")),
            @AttributeOverride(name = "latitude", column = @Column(name = "pickup_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "pickup_longitude"))
    })
    private Address pickupAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "addressLine1", column = @Column(name = "delivery_address_line1")),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "delivery_address_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_city")),
            @AttributeOverride(name = "state", column = @Column(name = "delivery_state")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "delivery_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "delivery_country")),
            @AttributeOverride(name = "latitude", column = @Column(name = "delivery_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "delivery_longitude"))
    })
    private Address deliveryAddress;

    @Column(name = "total_weight_kg", precision = 10, scale = 2)
    private BigDecimal totalWeightKg;

    @Column(name = "shipping_cost", precision = 19, scale = 4)
    private BigDecimal shippingCost;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "label_url")
    private String labelUrl;

    @Column(name = "estimated_delivery_date")
    private Instant estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private Instant actualDeliveryDate;

    @Column(name = "carrier_response", columnDefinition = "TEXT")
    private String carrierResponse;      // JSON from carrier

    @Column(name = "last_tracking_update")
    private Instant lastTrackingUpdate;

    // Domain methods
    public void markPickedUp() {
        this.status = ShipmentStatus.PICKED_UP;
    }

    public void markInTransit() {
        this.status = ShipmentStatus.IN_TRANSIT;
    }

    public void markOutForDelivery() {
        this.status = ShipmentStatus.OUT_FOR_DELIVERY;
    }

    public void markDelivered() {
        this.status = ShipmentStatus.DELIVERED;
        this.actualDeliveryDate = Instant.now();
    }

    public void markFailed(String reason) {
        this.status = ShipmentStatus.FAILED;
        this.carrierResponse = reason;
    }

    public void updateTracking(String newStatus, String details) {
        this.status = ShipmentStatus.valueOf(newStatus.toUpperCase());
        this.lastTrackingUpdate = Instant.now();
        this.carrierResponse = details;
    }
}