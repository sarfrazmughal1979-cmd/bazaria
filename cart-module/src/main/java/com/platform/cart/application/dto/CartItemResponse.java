package com.platform.cart.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private UUID itemId;
    private UUID productId;
    private String productName;
    private String productImage;
    private UUID variantId;
    private String variantName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private boolean inStock;
}