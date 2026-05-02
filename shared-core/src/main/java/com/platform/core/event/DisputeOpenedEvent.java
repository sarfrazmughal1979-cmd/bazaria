package com.platform.core.event;
import lombok.NoArgsConstructor;

import lombok.Getter;

@Getter
@NoArgsConstructor
public class DisputeOpenedEvent extends DomainEvent {

    private  String disputeId;
    private  String orderId;
    private  String customerId;
    private  String vendorId;
    private  String reason;

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