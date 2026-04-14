package com.platform.payment.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.payment.domain.model.Payment;
import com.platform.payment.domain.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends BaseRepository<Payment> {

    Optional<Payment> findByOrderId(UUID orderId);

    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);

    Page<Payment> findByCustomerId(UUID customerId, Pageable pageable);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, Instant createdAt);

    boolean existsByOrderIdAndStatus(UUID orderId, PaymentStatus status);

    @Modifying
    @Query("UPDATE Payment p SET p.status = :status WHERE p.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") PaymentStatus status);
}