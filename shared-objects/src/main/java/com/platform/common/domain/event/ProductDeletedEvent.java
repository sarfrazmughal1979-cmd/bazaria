package com.platform.common.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class ProductDeletedEvent extends DomainEvent {
    private final String productId;

    public ProductDeletedEvent(String productId) {
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