package com.platform.wishlist.application.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {
    private UUID id;
    private UUID productId;
    private UUID variantId;
    private String productName;
    private String productImage;
    private String notes;
    private Instant addedAt;
}