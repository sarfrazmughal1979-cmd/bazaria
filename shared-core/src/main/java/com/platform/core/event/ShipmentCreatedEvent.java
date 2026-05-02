package com.platform.core.event;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
 extends DomainEvent {

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