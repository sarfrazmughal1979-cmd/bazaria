package com.platform.settlement.domain.event;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
 extends DomainEvent {
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
