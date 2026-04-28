package com.platform.review.application.service;

import com.platform.review.application.dto.CreateReviewRequest;
import com.platform.review.application.dto.ReviewResponse;
import com.platform.review.application.mapper.ReviewMapper;
import com.platform.review.domain.model.Review;
import com.platform.review.domain.repository.ReviewRepository;
import com.platform.core.dto.PagedResponse;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.core.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper mapper;

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        UUID customerId = SecurityUtils.getCurrentUserId();
        Review review = mapper.toEntity(request, customerId);
        review = reviewRepository.save(review);
        return mapper.toResponse(review);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getProductReviews(UUID productId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByProductIdAndApprovedTrue(productId, pageable);
        return PagedResponse.from(page.map(mapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getMyReviews(Pageable pageable) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Page<Review> page = reviewRepository.findByCustomerId(userId, pageable);
        return PagedResponse.from(page.map(mapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getPendingReviews(Pageable pageable) {
        Page<Review> page = reviewRepository.findByApprovedFalse(pageable);
        return PagedResponse.from(page.map(mapper::toResponse));
    }

    @Transactional
    public void approveReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        review.approve();
        reviewRepository.save(review);
    }

    @Transactional
    public void rejectReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        review.reject();
        reviewRepository.save(review);
    }

    @Transactional
    public void markHelpful(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        review.markHelpful();
        reviewRepository.save(review);
    }

    @Transactional
    public void markNotHelpful(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        review.markNotHelpful();
        reviewRepository.save(review);
    }
}
