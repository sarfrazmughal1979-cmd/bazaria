package com.platform.shipping.application.service;

import com.platform.shipping.application.dto.ShippingRateRequest;
import com.platform.shipping.application.dto.ShippingRateResponse;
import com.platform.shipping.application.provider.ShippingProviderAdapter;
import com.platform.shipping.application.provider.ShippingProviderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingRateCalculator {

    private final ShippingProviderFactory providerFactory;

    public List<ShippingRateResponse> calculateRates(ShippingRateRequest request) {
        List<ShippingRateResponse> allRates = new ArrayList<>();
        for (String provider : new String[]{"TCS", "LEOPARD", "CALLCourier"}) {
            try {
                ShippingProviderAdapter adapter = providerFactory.getAdapter(provider);
                allRates.addAll(adapter.getRates(request));
            } catch (Exception e) {
                log.warn("Could not get rates from {}: {}", provider, e.getMessage());
            }
        }
        return allRates;
    }

    public ShippingRateResponse getCheapestRate(ShippingRateRequest request) {
        return calculateRates(request).stream()
                .min((r1, r2) -> r1.getCost().compareTo(r2.getCost()))
                .orElse(null);
    }
}