package com.platform.core.event;
import lombok.NoArgsConstructor;

import lombok.Getter;

@Getter
@NoArgsConstructor
public class OrderShippedEvent extends DomainEvent {
    private  String orderId;
    private  String subOrderId;
    private  String customerId;
    private  String trackingNumber;
    private  String carrier;

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