package com.platform.common.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
public class VendorApprovedEvent extends DomainEvent {

    private  String vendorId;
    private  String shopName;
    private  String userId;

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