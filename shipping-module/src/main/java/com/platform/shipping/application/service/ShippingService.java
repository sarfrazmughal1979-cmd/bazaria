package com.platform.shipping.application.service;

import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.dto.PagedResponse;
import com.platform.core.event.*;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.shipping.application.dto.*;
import com.platform.shipping.application.mapper.ShippingMapper;
import com.platform.shipping.application.provider.ShippingProviderAdapter;
import com.platform.shipping.application.provider.ShippingProviderFactory;
import com.platform.shipping.domain.model.Shipment;
import com.platform.shipping.domain.model.ShipmentStatus;
import com.platform.shipping.domain.repository.ShipmentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingService {

    private final ShipmentRepository shipmentRepository;
    private final ShippingProviderFactory providerFactory;
    private final ShippingRateCalculator rateCalculator;
    private final DomainEventPublisher eventPublisher;
    private final ShippingMapper shippingMapper;
    private final RestClientFactory restClientFactory;

    @Value("${module.order.url:http://localhost:8080}")
    private String orderBaseUrl;

    private ResilientRestClient orderRestClient;

    @PostConstruct
    public void init() {
        orderRestClient = restClientFactory.create(orderBaseUrl, 10);
    }

    // DTOs for Order module communication
    private record SubOrderInfo(UUID subOrderId, UUID orderId, UUID vendorId, String status, BigDecimal subtotal, UUID categoryId) {}
    private record AssignShipmentRequest(UUID shipmentId, String trackingNumber) {}

    @Transactional
    public ShipmentResponse createShipment(CreateShipmentRequest request) {
        // 1. Validate sub-order exists and is ready for shipping
        SubOrderInfo subOrderInfo = orderRestClient.get(
                "/api/v1/sub-orders/{subOrderId}/info-mini",
                SubOrderInfo.class,
                request.getSubOrderId());
        if (subOrderInfo == null) {
            throw new ResourceNotFoundException("SubOrder", "id", request.getSubOrderId());
        }
        if (!"CONFIRMED".equals(subOrderInfo.status()) && !"PROCESSING".equals(subOrderInfo.status())) {
            throw new BusinessException("INVALID_SUBORDER_STATUS",
                    "Sub-order must be confirmed or processing before shipping");
        }

        // 2. Calculate shipping rate
        ShippingRateRequest rateRequest = ShippingRateRequest.builder()
                .fromPostalCode(request.getPickupAddress().getPostalCode())
                .toPostalCode(request.getDeliveryAddress().getPostalCode())
                .weightKg(BigDecimal.valueOf(request.getWeightKg()))
                .country(request.getDeliveryAddress().getCountry())
                .city(request.getDeliveryAddress().getCity())
                .build();

        ShippingRateResponse bestRate;
        if (request.getCarrier() != null && !request.getCarrier().isEmpty()) {
            // Use specified carrier
            bestRate = providerFactory.getAdapter(request.getCarrier()).getRates(rateRequest).stream()
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("NO_RATE", "No rate available for carrier"));
        } else {
            bestRate = rateCalculator.getCheapestRate(rateRequest);
            if (bestRate == null) {
                throw new BusinessException("NO_SHIPPING_RATE", "Could not determine shipping rate");
            }
        }

        // 3. Create shipment via carrier adapter
        var adapter = providerFactory.getAdapter(bestRate.getCarrier());
        Shipment shipment = adapter.createShipment(request);
        shipment.setShippingCost(bestRate.getCost());
        shipment.setCurrency(bestRate.getCurrency());
        shipment.setCarrier(bestRate.getCarrier());

        Shipment saved = shipmentRepository.save(shipment);

        // 4. Update sub-order with shipment ID and tracking number via REST
        orderRestClient.post(
                "/api/v1/sub-orders/{subOrderId}/assign-shipment",
                new AssignShipmentRequest(saved.getId(), saved.getTrackingNumber()),
                Void.class,
                request.getSubOrderId());

        // 5. Publish event
        eventPublisher.publish(new ShipmentCreatedEvent(
                saved.getId().toString(),
                saved.getSubOrderId().toString(),
                saved.getTrackingNumber()
        ));

        return shippingMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentBySubOrder(UUID subOrderId) {
        Shipment shipment = shipmentRepository.findBySubOrderId(subOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "subOrderId", subOrderId));
        return shippingMapper.toResponse(shipment);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByTracking(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "trackingNumber", trackingNumber));
        return shippingMapper.toResponse(shipment);
    }

    @Transactional
    public void updateShipmentStatus(String trackingNumber, String newStatus, String details) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "trackingNumber", trackingNumber));

        ShipmentStatus newShipmentStatus = ShipmentStatus.valueOf(newStatus.toUpperCase());

        shipment.updateTracking(newStatus, details);
        shipmentRepository.save(shipment);

        // Publish appropriate event and notify Order module when delivered
        switch (newShipmentStatus) {
            case PICKED_UP -> eventPublisher.publish(new ShipmentPickedUpEvent(
                    shipment.getId().toString(), shipment.getTrackingNumber()));
            case IN_TRANSIT -> eventPublisher.publish(new ShipmentInTransitEvent(
                    shipment.getId().toString(), shipment.getTrackingNumber()));
            case OUT_FOR_DELIVERY -> eventPublisher.publish(new ShipmentOutForDeliveryEvent(
                    shipment.getId().toString(), shipment.getTrackingNumber()));
            case DELIVERED -> {
                shipment.markDelivered();
                eventPublisher.publish(new ShipmentDeliveredEvent(
                        shipment.getId().toString(), shipment.getTrackingNumber()));
                // Notify Order module to mark sub-order as delivered
                orderRestClient.post(
                        "/api/v1/sub-orders/{subOrderId}/mark-delivered",
                        null, Void.class, shipment.getSubOrderId());
            }
            default -> {}
        }
    }

    @Transactional(readOnly = true)
    public PagedResponse<ShipmentResponse> getVendorShipments(UUID vendorId, Pageable pageable) {
        Page<Shipment> page = shipmentRepository.findByVendorIdAndStatus(vendorId, null, pageable);
        return PagedResponse.from(page.map(shippingMapper::toResponse));
    }

    /**
     * Process incoming webhook from a shipping carrier.
     *
     * @param carrier the carrier name (e.g., "pathao", "redx")
     * @param payload raw JSON payload from the carrier
     */
    @Transactional
    public void processWebhook(String carrier, String payload) {
        log.info("Processing webhook from carrier: {}", carrier);
        ShippingProviderAdapter adapter = providerFactory.getAdapter(carrier);
        if (adapter.supportsWebhook()) {
            adapter.processWebhook(payload);
        } else {
            log.warn("Carrier {} does not support webhooks, ignoring", carrier);
        }
    }
}