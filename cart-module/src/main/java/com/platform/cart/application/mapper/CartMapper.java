package com.platform.cart.application.mapper;

import com.platform.cart.application.dto.CartItemResponse;
import com.platform.cart.application.dto.CartResponse;
import com.platform.cart.domain.model.Cart;
import com.platform.cart.domain.model.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        if (cart == null) return null;

        return CartResponse.builder()
                .cartId(cart.getId())
                .customerId(cart.getCustomerId())
                .items(mapCartItems(cart.getItems()))
                .couponCode(cart.getCouponCode())
                .discountAmount(cart.getDiscountAmount())
                .totalAmount(cart.getTotalAmount())
                .itemCount(cart.getItems() != null ? cart.getItems().size() : 0)
                .build();
    }

    public CartItemResponse toItemResponse(CartItem item) {
        if (item == null) return null;

        return CartItemResponse.builder()
                .itemId(item.getId())
                .productId(item.getProductId())
                .variantId(item.getVariantId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    private List<CartItemResponse> mapCartItems(List<CartItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    // Optional: if you need to enrich with product names/images from Catalog module
    // You can create a separate method that takes additional parameters
    public CartResponse enrichWithProductDetails(CartResponse cartResponse,
                                                 java.util.Map<java.util.UUID, ProductInfo> productInfoMap) {
        if (cartResponse == null || cartResponse.getItems() == null) return cartResponse;

        List<CartItemResponse> enrichedItems = cartResponse.getItems().stream()
                .map(item -> {
                    ProductInfo info = productInfoMap.get(item.getProductId());
                    if (info != null) {
                        item.setProductName(info.name());
                        item.setProductImage(info.imageUrl());
                        if (info.variantName() != null && item.getVariantId() != null) {
                            item.setVariantName(info.variantName());
                        }
                    }
                    return item;
                })
                .collect(Collectors.toList());

        cartResponse.setItems(enrichedItems);
        return cartResponse;
    }

    // Helper record for product info (can be moved to a shared DTO)
    public record ProductInfo(String name, String imageUrl, String variantName) {}
}