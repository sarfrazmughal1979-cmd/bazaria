package com.platform.cart.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {
    @NotNull
    private UUID productId;
    private UUID variantId;
    @Min(1)
    private int quantity;
}