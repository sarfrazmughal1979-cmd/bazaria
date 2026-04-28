package com.platform.wishlist.domain.repository;

import com.platform.wishlist.domain.model.WishlistItem;
import com.platform.core.repository.SoftDeleteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends SoftDeleteRepository<WishlistItem> {
    Page<WishlistItem> findByCustomerId(UUID customerId, Pageable pageable);
    Optional<WishlistItem> findByCustomerIdAndProductId(UUID customerId, UUID productId);
    long countByCustomerId(UUID customerId);
    void deleteByCustomerIdAndProductId(UUID customerId, UUID productId);
}