package com.platform.shipping.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class ShipmentTrackingUpdatedEvent extends DomainEvent {
    private  String shipmentId;
    private  String trackingNumber;
    private  String newStatus;

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
