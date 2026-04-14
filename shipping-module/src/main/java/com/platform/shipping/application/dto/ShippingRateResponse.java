package com.platform.shipping.application.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingRateResponse {
    private String carrier;
    private String method;
    private BigDecimal cost;
    private Integer estimatedDays;
    private String currency;
}