package com.platform.pricing.api;

import com.platform.core.dto.ApiResponse;
import com.platform.pricing.application.dto.CurrencyConversionRequest;
import com.platform.pricing.application.dto.CurrencyConversionResponse;
import com.platform.pricing.application.dto.TaxCalculationRequest;
import com.platform.pricing.application.dto.TaxCalculationResponse;
import com.platform.pricing.application.service.CurrencyService;
import com.platform.pricing.application.service.TaxService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final TaxService taxService;
    private final CurrencyService currencyService;

    @PostMapping("/tax/calculate")
    @Operation(summary = "Calculate tax for an order/cart")
    public ResponseEntity<ApiResponse<TaxCalculationResponse>> calculateTax(
            @Valid @RequestBody TaxCalculationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taxService.calculateTax(request)));
    }

    @PostMapping("/currency/convert")
    @Operation(summary = "Convert currency")
    public ResponseEntity<ApiResponse<CurrencyConversionResponse>> convertCurrency(
            @Valid @RequestBody CurrencyConversionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(currencyService.convert(request)));
    }
}