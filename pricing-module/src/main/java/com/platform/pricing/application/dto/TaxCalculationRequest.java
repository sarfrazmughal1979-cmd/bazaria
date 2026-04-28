package com.platform.pricing.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TaxCalculationRequest {
    @NotNull private BigDecimal subtotal;
    @NotBlank private String countryCode;
    private String stateCode;
    private UUID categoryId;
}