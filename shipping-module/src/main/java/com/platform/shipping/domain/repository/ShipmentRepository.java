package com.platform.shipping.domain.repository;

import com.platform.core.repository.BaseRepository;
import com.platform.shipping.domain.model.Shipment;
import com.platform.shipping.domain.model.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShipmentRepository extends BaseRepository<Shipment> {

    Optional<Shipment> findBySubOrderId(UUID subOrderId);

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    Page<Shipment> findByVendorIdAndStatus(UUID vendorId, ShipmentStatus status, Pageable pageable);

    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);
}