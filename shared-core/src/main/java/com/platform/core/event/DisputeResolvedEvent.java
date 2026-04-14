package com.platform.core.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class DisputeResolvedEvent extends DomainEvent {

    private final String disputeId;
    private final String resolution;
    private final String resolvedBy;
    private final BigDecimal amount;

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