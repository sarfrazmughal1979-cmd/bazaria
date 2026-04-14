package com.platform.shipping.application.mapper;

import com.platform.shipping.application.dto.ShipmentResponse;
import com.platform.shipping.application.dto.CreateShipmentRequest;
import com.platform.shipping.domain.model.Shipment;
import com.platform.shipping.domain.model.ShipmentStatus;
import org.springframework.stereotype.Component;

@Component
public class ShippingMapper {

    public ShipmentResponse toResponse(Shipment shipment) {
        if (shipment == null) return null;

        return ShipmentResponse.builder()
                .shipmentId(shipment.getId())
                .subOrderId(shipment.getSubOrderId())
                .trackingNumber(shipment.getTrackingNumber())
                .carrier(shipment.getCarrier())
                .status(shipment.getStatus() != null ? shipment.getStatus().name() : null)
                .deliveryAddress(shipment.getDeliveryAddress())
                .shippingCost(shipment.getShippingCost())
                .labelUrl(shipment.getLabelUrl())
                .estimatedDeliveryDate(shipment.getEstimatedDeliveryDate())
                .actualDeliveryDate(shipment.getActualDeliveryDate())
                .build();
    }

    public Shipment toEntity(CreateShipmentRequest request) {
        if (request == null) return null;

        return Shipment.builder()
                .subOrderId(request.getSubOrderId())
                .vendorId(request.getVendorId())
                .pickupAddress(request.getPickupAddress())
                .deliveryAddress(request.getDeliveryAddress())
                .totalWeightKg(request.getWeightKg() != null ?
                        java.math.BigDecimal.valueOf(request.getWeightKg()) : null)
                .status(ShipmentStatus.PENDING)
                .build();
    }

    // Optional: update entity from request (for partial updates)
    public void updateEntity(Shipment shipment, CreateShipmentRequest request) {
        if (shipment == null || request == null) return;
        if (request.getPickupAddress() != null) shipment.setPickupAddress(request.getPickupAddress());
        if (request.getDeliveryAddress() != null) shipment.setDeliveryAddress(request.getDeliveryAddress());
        if (request.getWeightKg() != null)
            shipment.setTotalWeightKg(java.math.BigDecimal.valueOf(request.getWeightKg()));
        if (request.getCarrier() != null) shipment.setCarrier(request.getCarrier());
        if (request.getShippingMethod() != null) {
            try {
                shipment.setMethod(com.platform.shipping.domain.model.ShippingMethod.valueOf(request.getShippingMethod()));
            } catch (IllegalArgumentException ignored) {}
        }
    }
}