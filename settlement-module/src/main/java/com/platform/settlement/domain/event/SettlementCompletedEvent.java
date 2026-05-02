package com.platform.settlement.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
 public class SettlementCompletedEvent extends DomainEvent {
    private  String settlementId;

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
