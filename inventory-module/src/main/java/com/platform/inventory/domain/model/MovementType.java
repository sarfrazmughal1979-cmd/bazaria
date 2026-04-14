package com.platform.inventory.domain.model;

public enum MovementType {
    INBOUND,        // stock added (purchase, return)
    OUTBOUND,       // stock removed (sold, damaged)
    RESERVATION,    // stock reserved for order
    RELEASE,        // reservation released
    ADJUSTMENT,     // manual adjustment
    TRANSFER_IN,    // transferred from another warehouse
    TRANSFER_OUT    // transferred to another warehouse
}
