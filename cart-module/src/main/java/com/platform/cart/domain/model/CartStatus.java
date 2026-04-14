package com.platform.cart.domain.model;

public enum CartStatus {
    ACTIVE,
    CONVERTED,   // after order placed
    EXPIRED,
    ABANDONED
}
