package com.platform.pricing.application.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CurrencyConversionResponse {
    private BigDecimal originalAmount;
    private String fromCurrency;
    private BigDecimal convertedAmount;
    private String toCurrency;
    private BigDecimal exchangeRate;
}