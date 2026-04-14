package com.platform.common.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
public class VendorApprovedEvent extends DomainEvent {

    private final String vendorId;
    private final String shopName;
    private final String userId;

    public VendorApprovedEvent(String vendorId, String shopName, String userId) {
        super();
        this.vendorId = vendorId;
        this.shopName = shopName;
        this.userId = userId;
    }

    @Override
    public String getAggregateId() {
        return vendorId;
    }

    @Override
    public String getAggregateType() {
        return "Vendor";
    }
}