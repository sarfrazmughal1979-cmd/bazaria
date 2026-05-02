package com.platform.shipping.domain.event;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
 extends DomainEvent {
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
