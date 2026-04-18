package com.platform.order.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.order.domain.model.SubOrder;
import com.platform.order.domain.model.SubOrderStatus;
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
public interface SubOrderRepository extends BaseRepository<SubOrder> {

    Optional<SubOrder> findSubOrderByIdAndVendorId(UUID subOrderId, UUID vendorID);

    Optional<SubOrder> findBySubOrderNumber(String subOrderNumber);

    Optional<SubOrder> findByIdAndVendorId(UUID id, UUID vendorId);

    List<SubOrder> findByOrderId(UUID orderId);

    Page<SubOrder> findByVendorId(UUID vendorId, Pageable pageable);

    Page<SubOrder> findByVendorIdAndStatus(UUID vendorId, SubOrderStatus status, Pageable pageable);

    List<SubOrder> findByStatusAndDeliveredAtBetween(SubOrderStatus status, Instant start, Instant end);

    @Query("SELECT s FROM SubOrder s WHERE s.vendorId = :vendorId AND s.createdAt BETWEEN :start AND :end")
    List<SubOrder> findByVendorIdAndCreatedAtBetween(@Param("vendorId") UUID vendorId,
                                                     @Param("start") Instant start,
                                                     @Param("end") Instant end);

    @Query("SELECT s FROM SubOrder s WHERE s.status = :status AND s.createdAt <= :before")
    List<SubOrder> findByStatusAndCreatedAtBefore(@Param("status") SubOrderStatus status,
                                                  @Param("before") Instant before);
}