package com.platform.cart.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private UUID cartId;
    private UUID customerId;
    private List<CartItemResponse> items;
    private String couponCode;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private int itemCount;
}