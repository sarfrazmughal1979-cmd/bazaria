package com.platform.review.api;

import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import com.platform.review.application.dto.CreateReviewRequest;
import com.platform.review.application.dto.ReviewResponse;
import com.platform.review.application.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Submit a product review")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(@Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(reviewService.createReview(request)));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get approved reviews for a product")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getProductReviews(
            @PathVariable UUID productId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getProductReviews(productId, pageable)));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my reviews")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getMyReviews(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getMyReviews(pageable)));
    }

    @PostMapping("/{reviewId}/helpful")
    @Operation(summary = "Mark a review as helpful")
    public ResponseEntity<ApiResponse<Void>> markHelpful(@PathVariable UUID reviewId) {
        reviewService.markHelpful(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Marked as helpful"));
    }

    @PostMapping("/{reviewId}/not-helpful")
    @Operation(summary = "Mark a review as not helpful")
    public ResponseEntity<ApiResponse<Void>> markNotHelpful(@PathVariable UUID reviewId) {
        reviewService.markNotHelpful(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Marked as not helpful"));
    }
}
