package com.platform.cart.application.dto;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequest {
    private UUID itemId;
    @Min(1)
    private int quantity;
}