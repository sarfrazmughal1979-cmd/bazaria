package com.platform.pricing.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CurrencyConversionRequest {
    @NotNull private BigDecimal amount;
    @NotBlank private String fromCurrency;
    @NotBlank private String toCurrency;
}