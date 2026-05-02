package com.platform.core.event;
import lombok.NoArgsConstructor;

import lombok.Getter;

@Getter
@NoArgsConstructor
public class ShipmentCreatedEvent extends DomainEvent {

    private  String shipmentId;
    private  String subOrderId;
    private  String trackingNumber;

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