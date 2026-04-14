package com.platform.shipping.domain.model;

public enum ShipmentStatus {
    PENDING,        // Created but not yet sent to carrier
    LABEL_GENERATED,
    PICKED_UP,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    FAILED,
    RETURNED,
    CANCELLED
}