package com.platform.common.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class ProductUpdatedEvent extends DomainEvent {
    private final String productId;

    public ProductUpdatedEvent(String productId) {
        super();
        this.productId = productId;
    }

    @Override
    public String getAggregateId() {
        return productId;
    }

    @Override
    public String getAggregateType() {
        return "Product";
    }
}