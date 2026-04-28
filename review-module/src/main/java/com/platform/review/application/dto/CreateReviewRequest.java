package com.platform.review.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    @NotNull
    private UUID productId;
    private UUID orderId;
    @Min(1) @Max(5)
    private int rating;
    private String title;
    private String comment;
}
