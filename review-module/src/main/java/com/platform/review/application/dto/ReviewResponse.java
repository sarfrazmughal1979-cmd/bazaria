package com.platform.review.application.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private UUID id;
    private UUID productId;
    private UUID customerId;
    private String customerName;
    private int rating;
    private String title;
    private String comment;
    private boolean verifiedPurchase;
    private boolean approved;
    private int helpfulCount;
    private int notHelpfulCount;
    private Instant createdAt;
}
