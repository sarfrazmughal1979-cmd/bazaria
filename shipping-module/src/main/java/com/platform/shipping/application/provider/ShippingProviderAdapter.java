package com.platform.shipping.application.provider;

import com.platform.shipping.application.dto.CreateShipmentRequest;
import com.platform.shipping.application.dto.ShippingRateRequest;
import com.platform.shipping.application.dto.ShippingRateResponse;
import com.platform.shipping.domain.model.Shipment;

import java.util.List;

public interface ShippingProviderAdapter {
    String getProviderName();
    List<ShippingRateResponse> getRates(ShippingRateRequest request);
    Shipment createShipment(CreateShipmentRequest request);
    Shipment updateTracking(String trackingNumber);
    String generateLabel(Shipment shipment);
    boolean supportsWebhook();
    void processWebhook(String payload);
}