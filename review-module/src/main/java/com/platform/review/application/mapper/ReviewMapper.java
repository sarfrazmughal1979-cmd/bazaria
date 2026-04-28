package com.platform.review.application.mapper;

import com.platform.review.application.dto.CreateReviewRequest;
import com.platform.review.application.dto.ReviewResponse;
import com.platform.review.domain.model.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public Review toEntity(CreateReviewRequest request, java.util.UUID customerId) {
        return Review.builder()
                .productId(request.getProductId())
                .customerId(customerId)
                .orderId(request.getOrderId())
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .approved(false) // requires moderation by default
                .build();
    }

    public ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProductId())
                .customerId(review.getCustomerId())
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .verifiedPurchase(review.isVerifiedPurchase())
                .approved(review.isApproved())
                .helpfulCount(review.getHelpfulCount())
                .notHelpfulCount(review.getNotHelpfulCount())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
