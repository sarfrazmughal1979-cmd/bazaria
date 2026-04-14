package com.platform.core.event;

import lombok.Getter;

@Getter
public class ShipmentOutForDeliveryEvent extends DomainEvent {

    private final String shipmentId;
    private final String trackingNumber;

    public ShipmentOutForDeliveryEvent(String shipmentId, String trackingNumber) {
        super();
        this.shipmentId = shipmentId;
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