package com.platform.core.event;
import lombok.NoArgsConstructor;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
 public class DisputeResolvedEvent extends DomainEvent {

    private  String disputeId;
    private  String resolution;
    private  String resolvedBy;
    private  BigDecimal amount;

    public DisputeResolvedEvent(String disputeId, String resolution, String resolvedBy, BigDecimal amount) {
        super();
        this.disputeId = disputeId;
        this.resolution = resolution;
        this.resolvedBy = resolvedBy;
        this.amount = amount;
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