package com.platform.shipping.application.dto;

import com.platform.core.domain.Address;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShipmentRequest {
    @NotNull
    private UUID subOrderId;
    @NotNull
    private UUID vendorId;
    private Address pickupAddress;
    @NotNull
    private Address deliveryAddress;
    private String carrier;           // optional, system selects best if null
    private String shippingMethod;    // STANDARD, EXPRESS, etc.
    private Double weightKg;
}