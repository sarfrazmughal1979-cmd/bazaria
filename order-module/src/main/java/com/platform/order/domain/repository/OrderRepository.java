package com.platform.order.domain.repository;

import com.platform.core.repository.SoftDeleteRepository;
import com.platform.order.domain.model.Order;
import com.platform.order.domain.model.OrderStatus;
import com.platform.order.domain.model.SubOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends SoftDeleteRepository<Order> {

    Optional<SubOrder> findSubOrderByIdAndVendorId(UUID subOrderId, UUID vendorID);
    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByIdAndCustomerId(UUID id, UUID customerId);

    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND o.createdAt >= :since")
    List<Order> findByCustomerIdSince(@Param("customerId") UUID customerId, @Param("since") Instant since);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt <= :before")
    List<Order> findByStatusAndCreatedAtBefore(@Param("status") OrderStatus status, @Param("before") Instant before);

    long countByStatus(OrderStatus status);
}