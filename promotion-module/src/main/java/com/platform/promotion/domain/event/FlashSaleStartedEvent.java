package com.platform.promotion.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class FlashSaleStartedEvent extends DomainEvent {

    private final String flashSaleId;
    private final String flashSaleName;

    public FlashSaleStartedEvent(String flashSaleId, String flashSaleName) {
        super();
        this.flashSaleId = flashSaleId;
        this.flashSaleName = flashSaleName;
    }

    @Override
    public String getAggregateId() {
        return flashSaleId;
    }

    @Override
    public String getAggregateType() {
        return "FlashSale";
    }
}