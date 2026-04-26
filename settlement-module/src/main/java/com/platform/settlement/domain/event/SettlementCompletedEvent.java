package com.platform.settlement.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class SettlementCompletedEvent extends DomainEvent {
    private final String settlementId;

    public SettlementCompletedEvent(String settlementId) {
        super();
        this.settlementId = settlementId;
    }

    @Override
    public String getAggregateId() {
        return settlementId;
    }

    @Override
    public String getAggregateType() {
        return "Settlement";
    }
}
