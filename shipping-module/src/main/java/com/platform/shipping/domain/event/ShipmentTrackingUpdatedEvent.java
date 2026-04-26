package com.platform.shipping.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class ShipmentTrackingUpdatedEvent extends DomainEvent {
    private final String shipmentId;
    private final String trackingNumber;
    private final String newStatus;

    public ShipmentTrackingUpdatedEvent(String shipmentId, String trackingNumber, String newStatus) {
        super();
        this.shipmentId = shipmentId;
        this.trackingNumber = trackingNumber;
        this.newStatus = newStatus;
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
