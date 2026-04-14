package com.platform.order.domain.model;

public enum ReturnStatus {
    PENDING,
    APPROVED,
    REJECTED,
    COMPLETED,   // refund issued
    CANCELLED
}