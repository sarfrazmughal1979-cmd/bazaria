package com.platform.pricing.application.service;

import com.platform.pricing.application.dto.CurrencyConversionRequest;
import com.platform.pricing.application.dto.CurrencyConversionResponse;
import com.platform.pricing.domain.model.CurrencyExchange;
import com.platform.pricing.domain.repository.CurrencyExchangeRepository;
import com.platform.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyExchangeRepository exchangeRepository;

    public CurrencyConversionResponse convert(CurrencyConversionRequest request) {
        if (request.getFromCurrency().equals(request.getToCurrency())) {
            return CurrencyConversionResponse.builder()
                    .originalAmount(request.getAmount())
                    .fromCurrency(request.getFromCurrency())
                    .convertedAmount(request.getAmount())
                    .toCurrency(request.getToCurrency())
                    .exchangeRate(BigDecimal.ONE)
                    .build();
        }

        CurrencyExchange exchange = exchangeRepository
                .findByFromCurrencyAndToCurrency(request.getFromCurrency(), request.getToCurrency())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ExchangeRate", request.getFromCurrency() + "->" + request.getToCurrency(), "not found"));

        BigDecimal converted = request.getAmount().multiply(exchange.getRate())
                .setScale(2, RoundingMode.HALF_UP);

        return CurrencyConversionResponse.builder()
                .originalAmount(request.getAmount())
                .fromCurrency(request.getFromCurrency())
                .convertedAmount(converted)
                .toCurrency(request.getToCurrency())
                .exchangeRate(exchange.getRate())
                .build();
    }
}