package com.platform.common.domain.event;


import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class ProductViewedEvent extends DomainEvent {
    private final String productId;
    private final String vendorId;

    public ProductViewedEvent(String productId, String vendorId) {
        super();
        this.productId = productId;
        this.vendorId = vendorId;
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