package com.platform.promotion.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class FlashSaleStartedEvent extends DomainEvent {

    private  String flashSaleId;
    private  String flashSaleName;

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