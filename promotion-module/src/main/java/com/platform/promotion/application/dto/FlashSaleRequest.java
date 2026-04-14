package com.platform.promotion.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleRequest {

    @NotBlank(message = "Flash sale name is required")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Start time is required")
    private Instant startTime;

    @NotNull(message = "End time is required")
    private Instant endTime;

    @Valid
    @NotEmpty(message = "At least one product is required")
    private List<FlashSaleItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlashSaleItemRequest {
        @NotNull(message = "Product ID is required")
        private UUID productId;

        private UUID variantId;  // optional

        @NotNull(message = "Flash sale price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        private BigDecimal flashSalePrice;

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code")
        private String currency;

        @Min(value = 1, message = "Total quantity must be at least 1")
        private int totalQuantity;

        @Min(value = 1, message = "Limit per customer must be at least 1")
        private int limitPerCustomer;
    }
}