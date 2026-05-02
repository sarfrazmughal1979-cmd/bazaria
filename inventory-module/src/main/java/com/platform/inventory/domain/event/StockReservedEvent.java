package com.platform.inventory.domain.event;
import lombok.NoArgsConstructor;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
@NoArgsConstructor
 public class StockReservedEvent extends DomainEvent {

    private  String productId;
    private  int quantity;
    private  String reservationId;
    private  String orderId;      // optional – can be null if reserved without order

    /**
     * Event fired when stock is successfully reserved for an order or cart.
     *
     * @param productId     affected product ID
     * @param quantity      reserved quantity
     * @param reservationId unique ID of the reservation (for later confirmation or release)
     * @param orderId       order ID that caused the reservation (nullable)
     */
    public StockReservedEvent(String productId, int quantity,
                              String reservationId, String orderId) {
        super();
        this.productId = productId;
        this.quantity = quantity;
        this.reservationId = reservationId;
        this.orderId = orderId;
    }

    // Convenience constructor without order ID
    public StockReservedEvent(String productId, int quantity, String reservationId) {
        this(productId, quantity, reservationId, null);
    }

    @Override
    public String getAggregateId() {
        return productId;
    }

    @Override
    public String getAggregateType() {
        return "InventoryItem";
    }
}