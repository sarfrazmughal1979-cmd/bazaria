package com.platform.shipping.application.dto;

import com.platform.core.domain.Address;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
    private UUID shipmentId;
    private UUID subOrderId;
    private String trackingNumber;
    private String carrier;
    private String status;
    private Address deliveryAddress;
    private BigDecimal shippingCost;
    private String labelUrl;
    private Instant estimatedDeliveryDate;
    private Instant actualDeliveryDate;
}