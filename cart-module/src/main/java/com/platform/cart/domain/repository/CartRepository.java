package com.platform.cart.domain.repository;

import com.platform.cart.domain.model.Cart;
import com.platform.cart.domain.model.CartStatus;
import com.platform.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends BaseRepository<Cart> {

    Optional<Cart> findByCustomerIdAndStatus(UUID customerId, CartStatus status);

    Optional<Cart> findBySessionIdAndStatus(String sessionId, CartStatus status);

    List<Cart> findByStatusAndUpdatedAtBefore(CartStatus status, Instant updatedAt);

    @Modifying
    @Query("UPDATE Cart c SET c.status = :status WHERE c.expiresAt < :now AND c.status = 'ACTIVE'")
    int expireOldCarts(@Param("now") Instant now, @Param("status") CartStatus status);

    List<Cart> findByCustomerIdAndStatusNot(UUID customerId, CartStatus status);
}