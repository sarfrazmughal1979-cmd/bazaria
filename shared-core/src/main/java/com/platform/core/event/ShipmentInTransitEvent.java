package com.platform.core.event;
import lombok.NoArgsConstructor;

import lombok.Getter;

@Getter
@NoArgsConstructor
public class ShipmentInTransitEvent extends DomainEvent {

    private  String shipmentId;
    private  String trackingNumber;

    public ShipmentInTransitEvent(String shipmentId, String trackingNumber) {
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