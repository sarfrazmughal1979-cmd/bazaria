package com.platform.shipping.application.service;

import com.platform.shipping.application.dto.ShippingRateRequest;
import com.platform.shipping.application.dto.ShippingRateResponse;
import com.platform.shipping.application.provider.ShippingProviderFactory;
import com.platform.shipping.domain.model.DeliveryZone;
import com.platform.shipping.domain.repository.DeliveryZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingRateCalculator {

    private final ShippingProviderFactory providerFactory;
    private final DeliveryZoneRepository deliveryZoneRepository;

    public List<ShippingRateResponse> calculateRates(ShippingRateRequest request) {
        // First try to get rates from all active providers
        List<ShippingRateResponse> allRates = providerFactory.getAdapter("DUMMY").getRates(request);

        // Enrich with delivery zone info if needed
        Optional<DeliveryZone> zone = deliveryZoneRepository
                .findByCountryAndCityAndActiveTrue(request.getCountry(), request.getCity());
        if (zone.isPresent()) {
            BigDecimal zoneRate = zone.get().getBaseRate();
            // adjust rates based on zone
            allRates.forEach(rate -> rate.setCost(rate.getCost().add(zoneRate)));
        }
        return allRates;
    }

    public ShippingRateResponse getCheapestRate(ShippingRateRequest request) {
        return calculateRates(request).stream()
                .min((r1, r2) -> r1.getCost().compareTo(r2.getCost()))
                .orElse(null);
    }
}