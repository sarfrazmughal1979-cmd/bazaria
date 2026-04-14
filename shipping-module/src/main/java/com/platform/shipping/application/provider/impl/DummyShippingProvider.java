package com.platform.shipping.application.provider.impl;

import com.platform.shipping.application.dto.CreateShipmentRequest;
import com.platform.shipping.application.dto.ShippingRateRequest;
import com.platform.shipping.application.dto.ShippingRateResponse;
import com.platform.shipping.application.provider.ShippingProviderAdapter;
import com.platform.shipping.domain.model.Shipment;
import com.platform.shipping.domain.model.ShipmentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component("dummyShippingProvider")
public class DummyShippingProvider implements ShippingProviderAdapter {

    @Override
    public String getProviderName() {
        return "DUMMY";
    }

    @Override
    public List<ShippingRateResponse> getRates(ShippingRateRequest request) {
        return List.of(
                ShippingRateResponse.builder()
                        .carrier("Dummy Express")
                        .method("STANDARD")
                        .cost(BigDecimal.valueOf(50))
                        .estimatedDays(3)
                        .currency("BDT")
                        .build(),
                ShippingRateResponse.builder()
                        .carrier("Dummy Express")
                        .method("EXPRESS")
                        .cost(BigDecimal.valueOf(100))
                        .estimatedDays(1)
                        .currency("BDT")
                        .build()
        );
    }

    @Override
    public Shipment createShipment(CreateShipmentRequest request) {
        String trackingNumber = "DMY" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return Shipment.builder()
                .subOrderId(request.getSubOrderId())
                .vendorId(request.getVendorId())
                .trackingNumber(trackingNumber)
                .carrier(getProviderName())
                .status(ShipmentStatus.LABEL_GENERATED)
                .method(com.platform.shipping.domain.model.ShippingMethod.valueOf(request.getShippingMethod()))
                .pickupAddress(request.getPickupAddress())
                .deliveryAddress(request.getDeliveryAddress())
                .totalWeightKg(BigDecimal.valueOf(request.getWeightKg()))
                .shippingCost(BigDecimal.valueOf(50))
                .currency("BDT")
                .labelUrl("https://example.com/labels/" + trackingNumber + ".pdf")
                .estimatedDeliveryDate(Instant.now().plusSeconds(3 * 86400))
                .build();
    }

    @Override
    public Shipment updateTracking(String trackingNumber) {
        // In real implementation, call carrier API
        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(trackingNumber);
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setLastTrackingUpdate(Instant.now());
        return shipment;
    }

    @Override
    public String generateLabel(Shipment shipment) {
        return shipment.getLabelUrl();
    }

    @Override
    public boolean supportsWebhook() {
        return false;
    }

    @Override
    public void processWebhook(String payload) {
        // not implemented
    }
}