package com.platform.review.domain.repository;

import com.platform.review.domain.model.Review;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ReviewRepository extends SoftDeleteRepository<Review> {
    Page<Review> findByProductIdAndApprovedTrue(UUID productId, Pageable pageable);
    Page<Review> findByProductId(UUID productId, Pageable pageable);
    Page<Review> findByCustomerId(UUID customerId, Pageable pageable);
    Page<Review> findByApprovedFalse(Pageable pageable);
}
