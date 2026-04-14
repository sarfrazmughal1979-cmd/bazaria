package com.platform.payment.domain.model;

public enum PaymentStatus {
    INITIATED,
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED
}