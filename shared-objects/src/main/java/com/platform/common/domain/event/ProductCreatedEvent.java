package com.platform.common.domain.event;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
 extends DomainEvent {

    private final String productId;
    private final String vendorId;
    private final String productName;

    public ProductCreatedEvent(String productId, String vendorId, String productName) {
        super();
        this.productId = productId;
        this.vendorId = vendorId;
        this.productName = productName;
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