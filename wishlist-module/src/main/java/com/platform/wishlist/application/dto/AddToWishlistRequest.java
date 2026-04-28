package com.platform.wishlist.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AddToWishlistRequest {
    @NotNull private UUID productId;
    private UUID variantId;
    private String notes;
}