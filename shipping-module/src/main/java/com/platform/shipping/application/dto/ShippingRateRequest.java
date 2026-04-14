package com.platform.shipping.application.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingRateRequest {
    private String fromPostalCode;
    private String toPostalCode;
    private BigDecimal weightKg;
    private String country;
    private String city;
}