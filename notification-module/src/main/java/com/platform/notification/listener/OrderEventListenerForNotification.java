package com.platform.notification.listener;

import com.platform.common.domain.event.OrderPlacedEvent;
import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.event.OrderShippedEvent;
import com.platform.core.event.PaymentCompletedEvent;
import com.platform.notification.application.service.NotificationService;
import com.platform.notification.application.service.TemplateService;
import com.platform.notification.domain.model.NotificationChannel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListenerForNotification {

    private final NotificationService notificationService;
    private final TemplateService templateService;
    private final RestClientFactory restClientFactory;

    @Value("${module.iam.url:http://localhost:8080}")
    private String iamBaseUrl;

    @Value("${module.order.url:http://localhost:8080}")
    private String orderBaseUrl;

    private ResilientRestClient iamRestClient;
    private ResilientRestClient orderRestClient;

    @PostConstruct
    public void init() {
        iamRestClient = restClientFactory.create(iamBaseUrl, 10);
        orderRestClient = restClientFactory.create(orderBaseUrl, 10);
    }

    @Async
    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Handling OrderPlacedEvent for order: {}", event.getOrderNumber());

        // Fetch customer email via REST call to IAM module
        String customerEmail = null;
        try {
            customerEmail = iamRestClient.get(
                    "/api/v1/users/{userId}/email", String.class, UUID.fromString(event.getCustomerId()));
        } catch (Exception e) {
            log.warn("Failed to fetch email for customer {}: {}", event.getCustomerId(), e.getMessage());
        }

        Map<String, Object> templateData = Map.of(
                "orderNumber", event.getOrderNumber(),
                "totalAmount", event.getTotalAmount(),
                "customerEmail", customerEmail
        );

        notificationService.send(
                UUID.fromString(event.getCustomerId()),
                "ORDER_PLACED",
                templateData,
                NotificationChannel.EMAIL,
                NotificationChannel.PUSH,
                NotificationChannel.IN_APP
        );
    }

    @Async
    @EventListener
    public void handleOrderShipped(OrderShippedEvent event) {
        Map<String, Object> templateData = Map.of(
                "orderNumber", event.getOrderId(),
                "trackingNumber", event.getTrackingNumber(),
                "carrier", event.getCarrier()
        );

        notificationService.send(
                UUID.fromString(event.getCustomerId()),
                "ORDER_SHIPPED",
                templateData,
                NotificationChannel.EMAIL,
                NotificationChannel.PUSH,
                NotificationChannel.SMS
        );
    }

    @Async
    @EventListener
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        String orderId = event.getOrderId();
        UUID customerId = null;
        try {
            var orderInfo = orderRestClient.get("/api/v1/orders/{orderId}/info-mini", OrderInfo.class, UUID.fromString(orderId));
            if (orderInfo != null) customerId = orderInfo.customerId();
        } catch (Exception e) { log.warn("Order fetch failed"); return; }
        notificationService.send(customerId, "PAYMENT_CONFIRMED", Map.of("orderId", orderId, "amount", event.getAmount()), NotificationChannel.EMAIL);
    }
    private record OrderInfo(UUID orderId, String orderNumber, UUID customerId, java.math.BigDecimal totalAmount, String currency, String status) {}
}