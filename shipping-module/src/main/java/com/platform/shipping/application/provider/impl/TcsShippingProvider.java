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
@Component("tcsShippingProvider")
public class TcsShippingProvider implements ShippingProviderAdapter {

    private final ShipmentRepository shipmentRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${tcs.api-key:test-tcs-key}")
    private String apiKey;
    @Value("${tcs.api-password:test-tcs-password}")
    private String apiPassword;
    @Value("${tcs.api-base-url:https://apis.tcs.com.pk/ecod}")
    private String apiBaseUrl;
    @Value("${tcs.test-mode:true}")
    private boolean testMode;

    public TcsShippingProvider(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Override public String getProviderName() { return "TCS"; }

    @Override
    public List<ShippingRateResponse> getRates(ShippingRateRequest request) {
        BigDecimal base = BigDecimal.valueOf(70);
        BigDecimal perKg = request.getWeightKg() != null
                ? request.getWeightKg().multiply(BigDecimal.valueOf(10))
                : BigDecimal.ZERO;
        BigDecimal costStandard = base.add(perKg);
        return List.of(
                ShippingRateResponse.builder().carrier("TCS").method("STANDARD")
                        .cost(costStandard).estimatedDays(3).currency("PKR").build(),
                ShippingRateResponse.builder().carrier("TCS").method("EXPRESS")
                        .cost(costStandard.multiply(new BigDecimal("1.8"))).estimatedDays(1).currency("PKR").build()
        );
    }

    @Override
    public Shipment createShipment(CreateShipmentRequest request) {
        Map<String, Object> bookingData = new LinkedHashMap<>();
        bookingData.put("api_key", apiKey);
        bookingData.put("api_password", apiPassword);
        bookingData.put("test_mode", testMode);
        bookingData.put("order_id", request.getSubOrderId().toString());
        bookingData.put("weight_kg", request.getWeightKg());
        bookingData.put("pieces", 1);
        bookingData.put("cod_amount", 0);
        bookingData.put("consignee_name", request.getDeliveryAddress().getAddressLine1());
        bookingData.put("consignee_address", request.getDeliveryAddress().getFullAddress());
        bookingData.put("consignee_phone", "03000000000");
        bookingData.put("consignee_city", request.getDeliveryAddress().getCity());
        bookingData.put("service_type", request.getShippingMethod());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-Key", apiKey);
            headers.set("X-API-Password", apiPassword);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bookingData, headers);

            String bookUrl = apiBaseUrl + "/v1/bookings";
            ResponseEntity<Map> response = restTemplate.postForEntity(bookUrl, entity, Map.class);
            Map<String, Object> body = response.getBody();

            String trackingNumber = body != null ? String.valueOf(body.getOrDefault("consignmentNumber",
                    "TCS-" + UUID.randomUUID().toString().substring(0,8).toUpperCase())) : "TCS-UNKNOWN";
            String labelUrl = body != null ? String.valueOf(body.getOrDefault("labelUrl", "")) : "";

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
                            .findFirst().map(ShippingRateResponse::getCost).orElse(BigDecimal.valueOf(70)))
                    .currency("PKR").labelUrl(labelUrl)
                    .estimatedDeliveryDate(Instant.now().plusSeconds(3 * 86400))
                    .build();
        } catch (Exception e) {
            log.error("TCS booking API failed", e);
            String fbTracking = "TCS-FB-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
            return Shipment.builder()
                    .subOrderId(request.getSubOrderId()).vendorId(request.getVendorId())
                    .trackingNumber(fbTracking).carrier(getProviderName())
                    .status(ShipmentStatus.PENDING)
                    .totalWeightKg(BigDecimal.valueOf(request.getWeightKg()))
                    .shippingCost(BigDecimal.valueOf(70))
                    .currency("PKR").estimatedDeliveryDate(Instant.now().plusSeconds(3 * 86400))
                    .build();
        }
    }

    @Override
    public Shipment updateTracking(String trackingNumber) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-Key", apiKey);
            headers.set("X-API-Password", apiPassword);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String trackUrl = apiBaseUrl + "/v1/tracking/" + trackingNumber;
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
            log.error("TCS tracking failed for {}", trackingNumber, e);
            return null;
        }
    }

    @Override public String generateLabel(Shipment shipment) { return shipment.getLabelUrl(); }
    @Override public boolean supportsWebhook() { return true; }
    @Override
    public void processWebhook(String payload) {
        try {
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);
            String trackingNumber = String.valueOf(data.getOrDefault("consignmentNumber", ""));
            String status = String.valueOf(data.getOrDefault("status", ""));
            if ("Delivered".equalsIgnoreCase(status)) {
                shipmentRepository.findByTrackingNumber(trackingNumber).ifPresent(s -> {
                    s.markDelivered();
                    shipmentRepository.save(s);
                    log.info("TCS webhook: {} delivered", trackingNumber);
                });
            }
        } catch (Exception e) {
            log.error("TCS webhook error", e);
        }
    }
}