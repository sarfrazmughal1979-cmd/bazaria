package com.platform.shipping.application.provider.impl;

import com.platform.shipping.application.dto.CreateShipmentRequest;
import com.platform.shipping.application.dto.ShippingRateRequest;
import com.platform.shipping.application.dto.ShippingRateResponse;
import com.platform.shipping.application.provider.ShippingProviderAdapter;
import com.platform.shipping.domain.model.Shipment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component("pathaoShippingProvider")
public class PathaoShippingProvider implements ShippingProviderAdapter {

    @Value("${pathao.api-key:test}")
    private String apiKey;

    @Override
    public String getProviderName() { return "PATHAO"; }

    @Override
    public List<ShippingRateResponse> getRates(ShippingRateRequest request) {
        return List.of(ShippingRateResponse.builder()
                .carrier("PATHAO").method("STANDARD").cost(BigDecimal.valueOf(70)).estimatedDays(2).currency("BDT").build());
    }

    @Override
    public Shipment createShipment(CreateShipmentRequest request) {
        Shipment shipment = new Shipment();
        shipment.setTrackingNumber("PATHAO_" + UUID.randomUUID().toString().substring(0,8));
        return shipment;
    }

    @Override
    public Shipment updateTracking(String trackingNumber) { return null; }
    @Override
    public String generateLabel(Shipment shipment) { return "https://pathao.com/label/..."; }
    @Override
    public boolean supportsWebhook() { return true; }
    @Override
    public void processWebhook(String payload) { log.info("Pathao webhook: {}", payload); }
}
