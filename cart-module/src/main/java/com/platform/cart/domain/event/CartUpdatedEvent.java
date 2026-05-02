package com.platform.cart.domain.event;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
 extends DomainEvent {

    private final String cartId;
    private final String action;  // e.g., "ADD_ITEM", "REMOVE_ITEM", "UPDATE_QUANTITY", "APPLY_COUPON", "CLEAR"

    public CartUpdatedEvent(String cartId, String action) {
        super();
        this.cartId = cartId;
        this.action = action;
    }

    @Override
    public String getAggregateId() {
        return cartId;
    }

    @Override
    public String getAggregateType() {
        return "Cart";
    }
}