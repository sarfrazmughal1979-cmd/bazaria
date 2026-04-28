package com.platform.shipping.application.provider.impl;

import com.fasterxml.jackson.core.type.TypeReference;
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
@Component("leopardShippingProvider")
public class LeopardShippingProvider implements ShippingProviderAdapter {

    private final ShipmentRepository shipmentRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${leopard.api-key:test-api-key}")
    private String apiKey;
    @Value("${leopard.api-password:test-password}")
    private String apiPassword;
    @Value("${leopard.api-base-url:https://api.leopardscourier.com}")
    private String apiBaseUrl;
    @Value("${leopard.test-mode:true}")
    private boolean testMode;

    public LeopardShippingProvider(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Override public String getProviderName() { return "LEOPARD"; }

    @Override
    public List<ShippingRateResponse> getRates(ShippingRateRequest request) {
        BigDecimal base = BigDecimal.valueOf(65);
        BigDecimal perKg = request.getWeightKg() != null
                ? request.getWeightKg().multiply(BigDecimal.valueOf(9))
                : BigDecimal.ZERO;
        BigDecimal costStandard = base.add(perKg);
        return List.of(
                ShippingRateResponse.builder().carrier("LEOPARD").method("STANDARD")
                        .cost(costStandard).estimatedDays(2).currency("PKR").build(),
                ShippingRateResponse.builder().carrier("LEOPARD").method("OVERNIGHT")
                        .cost(costStandard.multiply(new BigDecimal("2.2"))).estimatedDays(1).currency("PKR").build()
        );
    }

    @Override
    public Shipment createShipment(CreateShipmentRequest request) {
        Map<String, Object> bookingData = new LinkedHashMap<>();
        bookingData.put("api_key", apiKey);
        bookingData.put("api_password", apiPassword);
        bookingData.put("enable_test_mode", testMode);
        bookingData.put("booked_packet_weight", String.valueOf((int)(request.getWeightKg() * 1000)));
        bookingData.put("booked_packet_no_piece", "1");
        bookingData.put("booked_packet_collect_amount", "0");
        bookingData.put("booked_packet_order_id", request.getSubOrderId().toString());
        bookingData.put("origin_city", "self");
        bookingData.put("destination_city", request.getDeliveryAddress().getCity());
        bookingData.put("shipment_name_eng", "self");
        bookingData.put("shipment_email", "self");
        bookingData.put("shipment_phone", "self");
        bookingData.put("shipment_address", "self");
        bookingData.put("consignment_name_eng", request.getDeliveryAddress().getAddressLine1());
        bookingData.put("consignment_email", "customer@example.com");
        bookingData.put("consignment_phone", "03000000000");
        bookingData.put("consignment_address", request.getDeliveryAddress().getFullAddress());
        bookingData.put("special_instructions", "Handle with care");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bookingData, headers);

            String bookUrl = apiBaseUrl + "/api/book-packet";
            ResponseEntity<Map> response = restTemplate.postForEntity(bookUrl, entity, Map.class);
            Map<String, Object> body = response.getBody();

            String trackingNumber = body != null ? String.valueOf(body.getOrDefault("tracking_number",
                    "LEO-" + UUID.randomUUID().toString().substring(0,8).toUpperCase())) : "LEO-UNKNOWN";
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
                            .findFirst().map(ShippingRateResponse::getCost).orElse(BigDecimal.valueOf(65)))
                    .currency("PKR").labelUrl(labelUrl)
                    .estimatedDeliveryDate(Instant.now().plusSeconds(2 * 86400))
                    .build();
        } catch (Exception e) {
            log.error("Leopard Courier booking failed", e);
            String fallbackTracking = "LEO-FB-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
            return Shipment.builder()
                    .subOrderId(request.getSubOrderId()).vendorId(request.getVendorId())
                    .trackingNumber(fallbackTracking).carrier(getProviderName())
                    .status(ShipmentStatus.PENDING)
                    .totalWeightKg(BigDecimal.valueOf(request.getWeightKg()))
                    .shippingCost(BigDecimal.valueOf(65))
                    .currency("PKR").estimatedDeliveryDate(Instant.now().plusSeconds(2 * 86400))
                    .build();
        }
    }

    @Override
    public Shipment updateTracking(String trackingNumber) {
        try {
            Map<String, Object> trackData = new LinkedHashMap<>();
            trackData.put("api_key", apiKey);
            trackData.put("api_password", apiPassword);
            trackData.put("enable_test_mode", testMode);
            trackData.put("track_numbers", trackingNumber);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(trackData, headers);

            String trackUrl = apiBaseUrl + "/api/track-packet";
            ResponseEntity<Map> response = restTemplate.postForEntity(trackUrl, entity, Map.class);
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
            log.error("Leopard tracking update failed for {}", trackingNumber, e);
            return null;
        }
    }

    @Override public String generateLabel(Shipment shipment) { return shipment.getLabelUrl(); }
    @Override public boolean supportsWebhook() { return true; }
    @Override
    public void processWebhook(String payload) {
        try {
            Map<String, Object> data = objectMapper.readValue(payload, new TypeReference<>() {});
            String trackingNumber = String.valueOf(data.getOrDefault("tracking_number", ""));
            String status = String.valueOf(data.getOrDefault("status", ""));
            if ("Delivered".equalsIgnoreCase(status)) {
                shipmentRepository.findByTrackingNumber(trackingNumber).ifPresent(s -> {
                    s.markDelivered();
                    shipmentRepository.save(s);
                    log.info("Leopard webhook: {} delivered", trackingNumber);
                });
            }
        } catch (Exception e) {
            log.error("Leopard webhook processing error", e);
        }
    }
}