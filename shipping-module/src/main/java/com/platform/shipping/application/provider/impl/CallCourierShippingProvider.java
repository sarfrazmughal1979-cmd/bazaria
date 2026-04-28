package com.platform.shipping.application.provider.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.shipping.application.dto.*;
import com.platform.shipping.application.provider.ShippingProviderAdapter;
import com.platform.shipping.domain.model.Shipment;
import com.platform.shipping.domain.model.ShipmentStatus;
import com.platform.shipping.domain.repository.ShipmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component("callCourierShippingProvider")
public class CallCourierShippingProvider implements ShippingProviderAdapter {

    private final ShipmentRepository shipmentRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${callcourier.api-key:test-cc-key}")
    private String apiKey;
    @Value("${callcourier.api-password:test-cc-password}")
    private String apiPassword;
    @Value("${callcourier.api-base-url:https://api.callcourier.com.pk}")
    private String apiBaseUrl;
    @Value("${callcourier.test-mode:true}")
    private boolean testMode;

    public CallCourierShippingProvider(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Override public String getProviderName() { return "CALLCourier"; }

    @Override
    public List<ShippingRateResponse> getRates(ShippingRateRequest request) {
        BigDecimal base = BigDecimal.valueOf(55);
        BigDecimal perKg = request.getWeightKg() != null
                ? request.getWeightKg().multiply(BigDecimal.valueOf(8))
                : BigDecimal.ZERO;
        BigDecimal cost = base.add(perKg);
        return List.of(
                ShippingRateResponse.builder().carrier("CALLCourier").method("STANDARD")
                        .cost(cost).estimatedDays(4).currency("PKR").build(),
                ShippingRateResponse.builder().carrier("CALLCourier").method("EXPRESS")
                        .cost(cost.multiply(new BigDecimal("1.5"))).estimatedDays(2).currency("PKR").build()
        );
    }

    @Override
    public Shipment createShipment(CreateShipmentRequest request) {
        Map<String, Object> bookingData = new LinkedHashMap<>();
        bookingData.put("api_key", apiKey);
        bookingData.put("api_password", apiPassword);
        bookingData.put("test_mode", testMode);
        bookingData.put("order_ref", request.getSubOrderId().toString());
        bookingData.put("weight_kg", request.getWeightKg());
        bookingData.put("pieces", 1);
        bookingData.put("cod_amount", 0);
        bookingData.put("receiver_name", request.getDeliveryAddress().getAddressLine1());
        bookingData.put("receiver_address", request.getDeliveryAddress().getFullAddress());
        bookingData.put("receiver_phone", "03000000000");
        bookingData.put("destination_city", request.getDeliveryAddress().getCity());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bookingData, headers);

            String bookUrl = apiBaseUrl + "/api/v1/shipments";
            ResponseEntity<Map> response = restTemplate.postForEntity(bookUrl, entity, Map.class);
            Map<String, Object> body = response.getBody();

            String trackingNumber = body != null ? String.valueOf(body.getOrDefault("tracking_id",
                    "CALL-" + UUID.randomUUID().toString().substring(0,8).toUpperCase())) : "CALL-UNKNOWN";
            String labelUrl = body != null ? String.valueOf(body.getOrDefault("label_url", "")) : "";

            return Shipment.builder()
                    .subOrderId(request.getSubOrderId()).vendorId(request.getVendorId())
                    .trackingNumber(trackingNumber).carrier(getProviderName())
                    .status(ShipmentStatus.PENDING)
                    .method(com.platform.shipping.domain.model.ShippingMethod.valueOf(request.getShippingMethod()))
                    .pickupAddress(request.getPickupAddress()).deliveryAddress(request.getDeliveryAddress())
                    .totalWeightKg(BigDecimal.valueOf(request.getWeightKg()))
                    .shippingCost(getRates(ShippingRateRequest.builder()
                            .weightKg(BigDecimal.valueOf(request.getWeightKg()))
                            .country(request.getDeliveryAddress().getCountry())
                            .city(request.getDeliveryAddress().getCity()).build())
                            .stream().filter(r -> r.getMethod().equals(request.getShippingMethod()))
                            .findFirst().map(ShippingRateResponse::getCost).orElse(BigDecimal.valueOf(55)))
                    .currency("PKR").labelUrl(labelUrl)
                    .estimatedDeliveryDate(Instant.now().plusSeconds(4 * 86400))
                    .build();
        } catch (Exception e) {
            log.error("CallCourier booking failed", e);
            String fbTracking = "CALL-FB-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
            return Shipment.builder()
                    .subOrderId(request.getSubOrderId()).vendorId(request.getVendorId())
                    .trackingNumber(fbTracking).carrier(getProviderName())
                    .status(ShipmentStatus.PENDING)
                    .totalWeightKg(BigDecimal.valueOf(request.getWeightKg()))
                    .shippingCost(BigDecimal.valueOf(55))
                    .currency("PKR").estimatedDeliveryDate(Instant.now().plusSeconds(4 * 86400))
                    .build();
        }
    }

    @Override
    public Shipment updateTracking(String trackingNumber) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String trackUrl = apiBaseUrl + "/api/v1/tracking/" + trackingNumber;
            ResponseEntity<Map> response = restTemplate.exchange(trackUrl, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && "Delivered".equalsIgnoreCase(String.valueOf(body.getOrDefault("status", "")))) {
                Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber).orElse(null);
                if (shipment != null) {
                    shipment.setStatus(ShipmentStatus.DELIVERED);
                    return shipment;
                }
            }
            return null;
        } catch (Exception e) {
            log.error("CallCourier tracking failed for {}", trackingNumber, e);
            return null;
        }
    }

    @Override public String generateLabel(Shipment shipment) { return shipment.getLabelUrl(); }
    @Override public boolean supportsWebhook() { return false; }
    @Override public void processWebhook(String payload) {}
}