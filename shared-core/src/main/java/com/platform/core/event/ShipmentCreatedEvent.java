package com.platform.core.event;

import lombok.Getter;

@Getter
public class ShipmentCreatedEvent extends DomainEvent {

    private final String shipmentId;
    private final String subOrderId;
    private final String trackingNumber;

    public ShipmentCreatedEvent(String shipmentId, String subOrderId, String trackingNumber) {
        super();
        this.shipmentId = shipmentId;
        this.subOrderId = subOrderId;
        this.trackingNumber = trackingNumber;
    }

    @Override
    public String getAggregateId() {
        return shipmentId;
    }

    @Override
    public String getAggregateType() {
        return "Shipment";
    }
}