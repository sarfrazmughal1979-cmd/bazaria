package com.platform.inventory.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    private UUID variantId;   // optional

    private UUID warehouseId; // optional

    @NotNull(message = "New quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    private Integer reorderPoint;  // optional – update threshold

    private String reason;         // e.g., "manual adjustment", "return restocked"
}