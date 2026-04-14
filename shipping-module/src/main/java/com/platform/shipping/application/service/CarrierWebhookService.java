package com.platform.shipping.application.service;

import com.platform.shipping.application.dto.CarrierWebhookPayload;
import com.platform.shipping.application.provider.ShippingProviderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarrierWebhookService {

    private final ShippingProviderFactory providerFactory;
    private final ShippingService shippingService;

    public void processWebhook(String carrier, String payload) {
        var adapter = providerFactory.getAdapter(carrier);
        if (adapter.supportsWebhook()) {
            adapter.processWebhook(payload);
        } else {
            // Fallback: manually parse and update
            // This would parse the payload and call shippingService.updateShipmentStatus
            log.info("Webhook received for carrier {} (manual processing)", carrier);
        }
    }
}