package com.platform.wishlist.application.mapper;

import com.platform.wishlist.application.dto.AddToWishlistRequest;
import com.platform.wishlist.application.dto.WishlistItemResponse;
import com.platform.wishlist.domain.model.WishlistItem;
import org.springframework.stereotype.Component;

@Component
public class WishlistMapper {

    public WishlistItem toEntity(AddToWishlistRequest request, java.util.UUID customerId) {
        return WishlistItem.builder()
                .customerId(customerId)
                .productId(request.getProductId())
                .variantId(request.getVariantId())
                .notes(request.getNotes())
                .addedFrom("API")
                .build();
    }

    public WishlistItemResponse toResponse(WishlistItem item) {
        return WishlistItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .variantId(item.getVariantId())
                .notes(item.getNotes())
                .addedAt(item.getCreatedAt())
                .build();
    }
}