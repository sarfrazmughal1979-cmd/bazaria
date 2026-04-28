package com.platform.pricing.application.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TaxCalculationResponse {
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private BigDecimal appliedRate;
    private String taxType;
    private String currency;
}