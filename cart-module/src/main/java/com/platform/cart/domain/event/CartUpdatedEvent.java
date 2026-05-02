package com.platform.cart.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

@Getter
@NoArgsConstructor
 public class CartUpdatedEvent extends DomainEvent {

    private  String cartId;
    private  String action;  // e.g., "ADD_ITEM", "REMOVE_ITEM", "UPDATE_QUANTITY", "APPLY_COUPON", "CLEAR"

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