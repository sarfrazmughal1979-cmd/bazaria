package com.platform.review.api;

import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import com.platform.review.application.dto.ReviewResponse;
import com.platform.review.application.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping("/pending")
    @Operation(summary = "Get unapproved reviews")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getPendingReviews(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getPendingReviews(pageable)));
    }

    @PutMapping("/{reviewId}/approve")
    @Operation(summary = "Approve a review")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable UUID reviewId) {
        reviewService.approveReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Review approved"));
    }

    @PutMapping("/{reviewId}/reject")
    @Operation(summary = "Reject a review")
    public ResponseEntity<ApiResponse<Void>> reject(@PathVariable UUID reviewId) {
        reviewService.rejectReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Review rejected"));
    }
}
