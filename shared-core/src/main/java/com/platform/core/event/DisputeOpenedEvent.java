package com.platform.core.event;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
 extends DomainEvent {

    private final String disputeId;
    private final String orderId;
    private final String customerId;
    private final String vendorId;
    private final String reason;

    public DisputeOpenedEvent(String disputeId, String orderId, String customerId, String vendorId, String reason) {
        super();
        this.disputeId = disputeId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.vendorId = vendorId;
        this.reason = reason;
    }

    @Override
    public String getAggregateId() {
        return disputeId;
    }

    @Override
    public String getAggregateType() {
        return "Dispute";
    }
}