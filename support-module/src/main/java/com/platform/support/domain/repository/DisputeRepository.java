package com.platform.support.domain.repository;

import com.platform.core.repository.SoftDeleteRepository;
import com.platform.support.domain.model.Dispute;
import com.platform.support.domain.model.DisputeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisputeRepository extends SoftDeleteRepository<Dispute> {

    Optional<Dispute> findByDisputeNumber(String disputeNumber);

    Page<Dispute> findByCustomerId(UUID customerId, Pageable pageable);

    Page<Dispute> findByVendorId(UUID vendorId, Pageable pageable);

    Page<Dispute> findByStatus(DisputeStatus status, Pageable pageable);

    Optional<Dispute> findByOrderIdAndSubOrderId(UUID orderId, UUID subOrderId);
}