package com.platform.core.event;

import lombok.Getter;

@Getter
public class OrderShippedEvent extends DomainEvent {
    private final String orderId;
    private final String subOrderId;
    private final String customerId;
    private final String trackingNumber;
    private final String carrier;

    public OrderShippedEvent(String orderId, String subOrderId, String customerId, String trackingNumber, String carrier) {
        super();
        this.orderId = orderId;
        this.subOrderId = subOrderId;
        this.customerId = customerId;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
    }

    @Override
    public String getAggregateId() {
        return orderId;
    }

    @Override
    public String getAggregateType() {
        return "Order";
    }
}