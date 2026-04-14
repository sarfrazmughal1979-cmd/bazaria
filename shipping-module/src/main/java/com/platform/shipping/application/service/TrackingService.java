package com.platform.shipping.application.service;

import com.platform.shipping.application.dto.TrackingResponse;
import com.platform.shipping.application.provider.ShippingProviderFactory;
import com.platform.shipping.domain.model.Shipment;
import com.platform.shipping.domain.model.ShipmentStatus;
import com.platform.shipping.domain.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingService {

    private final ShipmentRepository shipmentRepository;
    private final ShippingProviderFactory providerFactory;

    public TrackingResponse getTrackingInfo(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        var adapter = providerFactory.getAdapter(shipment.getCarrier());
        // In real implementation, call carrier API to get detailed events
        Shipment updated = adapter.updateTracking(trackingNumber);
        shipment.setStatus(updated.getStatus());
        shipment.setLastTrackingUpdate(Instant.now());
        shipmentRepository.save(shipment);

        return TrackingResponse.builder()
                .trackingNumber(shipment.getTrackingNumber())
                .carrier(shipment.getCarrier())
                .status(shipment.getStatus().name())
                .events(List.of()) // would be filled from carrier response
                .build();
    }

    @Scheduled(fixedDelay = 3600000) // every hour
    public void pollPendingShipments() {
        // For carriers that don't support webhooks, poll for updates
        List<Shipment> pending = shipmentRepository.findByStatus(ShipmentStatus.PICKED_UP, Pageable.unpaged()).getContent();
        for (Shipment shipment : pending) {
            try {
                getTrackingInfo(shipment.getTrackingNumber());
            } catch (Exception e) {
                log.error("Failed to poll tracking for {}", shipment.getTrackingNumber(), e);
            }
        }
    }
}