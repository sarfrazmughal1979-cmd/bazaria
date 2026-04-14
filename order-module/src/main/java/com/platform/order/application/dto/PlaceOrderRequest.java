package com.platform.order.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String couponCode;

    private String note;

    @NotNull
    @Valid
    private ShippingAddressRequest shippingAddress;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingAddressRequest {
        @NotBlank private String addressLine1;
        private String addressLine2;
        @NotBlank private String city;
        private String state;
        private String postalCode;
        @NotBlank private String country;
    }
}