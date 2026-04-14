package com.platform.inventory.domain.event;

import com.platform.core.event.DomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class StockDepletedEvent extends DomainEvent {

    private final String productId;
    private final String variantId;   // can be null for simple products
    private final int remainingStock;
    private final int reorderPoint;

    /**
     * Event fired when inventory stock falls below or equal to reorder point.
     *
     * @param productId      affected product ID
     * @param variantId      variant ID (null if product has no variants)
     * @param remainingStock current available quantity
     * @param reorderPoint   configured threshold that triggered the event
     */
    public StockDepletedEvent(String productId, String variantId, int remainingStock, int reorderPoint) {
        super();
        this.productId = productId;
        this.variantId = variantId;
        this.remainingStock = remainingStock;
        this.reorderPoint = reorderPoint;
    }

    // Convenience constructor for non‑variant products
    public StockDepletedEvent(String productId, int remainingStock, int reorderPoint) {
        this(productId, null, remainingStock, reorderPoint);
    }

    @Override
    public String getAggregateId() {
        return productId + (variantId != null ? ":" + variantId : "");
    }

    @Override
    public String getAggregateType() {
        return "InventoryItem";
    }
}