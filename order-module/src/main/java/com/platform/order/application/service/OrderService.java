package com.platform.order.application.service;

import com.platform.common.domain.event.OrderPlacedEvent;
import com.platform.core.client.RestClient;
import com.platform.core.client.RestClientFactory;
import com.platform.core.domain.Money;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.core.security.SecurityUtils;
import com.platform.order.application.dto.*;
import com.platform.order.application.mapper.OrderMapper;
import com.platform.order.domain.model.*;
import com.platform.order.domain.repository.OrderRepository;
import com.platform.order.domain.repository.SubOrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final SubOrderRepository subOrderRepository;
    private final OrderMapper orderMapper;
    private final DomainEventPublisher eventPublisher;

    private final RestClientFactory restClientFactory;

    @Value("${module.cart.url:http://localhost:8080}")
    private String cartBaseUrl;

    @Value("${module.catalog.url:http://localhost:8080}")
    private String catalogBaseUrl;

    @Value("${module.inventory.url:http://localhost:8080}")
    private String inventoryBaseUrl;

    @Value("${module.promotion.url:http://localhost:8080}")
    private String promotionBaseUrl;

    @Value("${module.payment.url:http://localhost:8080}")
    private String paymentBaseUrl;  // not used in this class, but kept for consistency

    private RestClient cartRestClient;
    private RestClient catalogRestClient;
    private RestClient inventoryRestClient;
    private RestClient promotionRestClient;

    @PostConstruct
    public void init() {
        cartRestClient = restClientFactory.create(cartBaseUrl, 10);
        catalogRestClient = restClientFactory.create(catalogBaseUrl, 10);
        inventoryRestClient = restClientFactory.create(inventoryBaseUrl, 10);
        promotionRestClient = restClientFactory.create(promotionBaseUrl, 10);
    }

    // ========== DTOs for REST communication (local to this service) ==========
    private record CartResponse(List<CartItem> items) {}
    private record CartItem(UUID productId, UUID variantId, int quantity) {}

    private record CatalogProductInfo(UUID productId, String name, BigDecimal effectivePrice,
                                      UUID vendorId, String imageUrl, String sku) {}

    private record ReserveStockResponse(UUID reservationId) {}

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        UUID customerId = SecurityUtils.getCurrentUserId();

        // 1. Get cart items via REST
        CartResponse cart = cartRestClient.get(
                "/api/v1/cart?customerId={customerId}", CartResponse.class, customerId);
        if (cart == null || cart.items().isEmpty()) {
            throw new BusinessException("EMPTY_CART", "Cart is empty");
        }

        // 2. Validate stock and reserve inventory via REST
        List<UUID> reservationIds = new ArrayList<>();
        try {
            for (CartItem cartItem : cart.items()) {
                ReserveStockResponse response = inventoryRestClient.post(
                        "/api/internal/inventory/reserve",
                        new ReserveStockRequest(cartItem.productId(), cartItem.variantId(), cartItem.quantity()),
                        ReserveStockResponse.class);
                reservationIds.add(response.reservationId());
            }
        } catch (Exception e) {
            reservationIds.forEach(id -> inventoryRestClient.post(
                    "/api/internal/inventory/release/{reservationId}", null, Void.class, id));
            throw new BusinessException("STOCK_UNAVAILABLE", "Some items are out of stock");
        }

        // 3. Calculate totals and group by vendor
        String currency = "BDT";
        Money subtotal = Money.zero(currency);
        Map<UUID, List<OrderItemInfo>> vendorItems = new HashMap<>();

        for (CartItem cartItem : cart.items()) {
            CatalogProductInfo productInfo = catalogRestClient.get(
                    "/api/v1/products/{productId}/info", CatalogProductInfo.class, cartItem.productId());

            Money itemTotal = Money.of(productInfo.effectivePrice()
                    .multiply(BigDecimal.valueOf(cartItem.quantity())), currency);
            subtotal = subtotal.add(itemTotal);

            vendorItems.computeIfAbsent(productInfo.vendorId(), k -> new ArrayList<>())
                    .add(new OrderItemInfo(cartItem, productInfo, itemTotal));
        }

        // 4. Apply coupon via REST
        Money discount = Money.zero(currency);
        if (request.getCouponCode() != null) {
            BigDecimal discountAmount = promotionRestClient.post(
                    "/api/internal/promotions/calculate-discount",
                    new DiscountRequest(request.getCouponCode(), subtotal.getAmount(), customerId),
                    BigDecimal.class);
            discount = Money.of(discountAmount, currency);
        }

        // 5. Shipping, tax, total
        Money shippingCost = calculateShipping(request, vendorItems);
        Money tax = subtotal.percentage(BigDecimal.valueOf(5));
        Money total = subtotal.add(shippingCost).add(tax).subtract(discount);

        // 6. Create order
        Order order = Order.builder()
                .orderNumber(Order.generateOrderNumber())
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .discountAmount(discount)
                .taxAmount(tax)
                .totalAmount(total)
                .couponCode(request.getCouponCode())
                .paymentMethod(request.getPaymentMethod())
                .shippingAddress(orderMapper.toAddress(request.getShippingAddress()))
                .customerNote(request.getNote())
                .build();
        order.addTimelineEntry("PENDING", "Order placed successfully");

        // 7. Create sub-orders
        for (var entry : vendorItems.entrySet()) {
            UUID vendorId = entry.getKey();
            List<OrderItemInfo> items = entry.getValue();

            Money subOrderTotal = items.stream()
                    .map(i -> i.itemTotal)
                    .reduce(Money.zero(currency), Money::add);

            SubOrder subOrder = SubOrder.builder()
                    .subOrderNumber(Order.generateOrderNumber())
                    .vendorId(vendorId)
                    .status(SubOrderStatus.PENDING)
                    .subtotal(subOrderTotal)
                    .shippingCost(Money.zero(currency))
                    .build();

            for (OrderItemInfo itemInfo : items) {
                OrderItem orderItem = OrderItem.builder()
                        .productId(itemInfo.cartItem.productId())
                        .variantId(itemInfo.cartItem.variantId())
                        .productName(itemInfo.productInfo.name())
                        .productImage(itemInfo.productInfo.imageUrl())
                        .sku(itemInfo.productInfo.sku())
                        .quantity(itemInfo.cartItem.quantity())
                        .unitPrice(Money.of(itemInfo.productInfo.effectivePrice(), currency))
                        .totalPrice(itemInfo.itemTotal)
                        .build();
                subOrder.addItem(orderItem);
            }
            order.addSubOrder(subOrder);
        }

        Order savedOrder = orderRepository.save(order);

        // 8. Clear cart via REST
        cartRestClient.delete("/api/v1/cart/clear?customerId={customerId}", customerId);

        // 9. Apply coupon usage via REST
        if (request.getCouponCode() != null) {
            promotionRestClient.post("/api/internal/promotions/apply-coupon",
                    new ApplyCouponRequest(request.getCouponCode(), customerId, savedOrder.getId()),
                    Void.class);
        }

        // 10. Publish event
        eventPublisher.publish(new OrderPlacedEvent(
                savedOrder.getId().toString(),
                customerId.toString(),
                savedOrder.getOrderNumber(),
                savedOrder.getTotalAmount().getAmount()
        ));

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        UUID customerId = SecurityUtils.getCurrentUserId();
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found"));
        return orderMapper.toResponse(order);
    }

    @Transactional
    public void updateSubOrderStatus(UUID subOrderId, OrderStatusUpdateRequest request) {
        UUID vendorId = SecurityUtils.getCurrentVendorId()
                .orElseThrow(() -> new BusinessException("NOT_VENDOR", "Not a vendor"));

        SubOrder subOrder = subOrderRepository.findSubOrderByIdAndVendorId(subOrderId, vendorId)
                .orElseThrow(() -> new BusinessException("SUBORDER_NOT_FOUND", "Sub-order not found"));

        switch (request.getStatus()) {
            case "PROCESSING" -> subOrder.setStatus(SubOrderStatus.PROCESSING);
            case "SHIPPED" -> subOrder.markAsShipped(request.getTrackingNumber());
            case "DELIVERED" -> {
                subOrder.markAsDelivered();
                checkOrderCompletion(subOrder.getOrder());
            }
            default -> throw new BusinessException("INVALID_STATUS", "Invalid status");
        }

        orderRepository.save(subOrder.getOrder());
    }

    private void checkOrderCompletion(Order order) {
        boolean allDelivered = order.getSubOrders().stream()
                .allMatch(so -> so.getStatus() == SubOrderStatus.DELIVERED);
        if (allDelivered) {
            order.setStatus(OrderStatus.DELIVERED);
            order.addTimelineEntry("DELIVERED", "All items have been delivered");
        }
    }

    private Money calculateShipping(PlaceOrderRequest request,
                                    Map<UUID, List<OrderItemInfo>> vendorItems) {
        return Money.bdt(BigDecimal.valueOf(vendorItems.size() * 60));
    }


    @Transactional
    public void confirmOrder(UUID orderId, UUID paymentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("INVALID_STATUS", "Order cannot be confirmed in current state");
        }
        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentId(paymentId);
        order.addTimelineEntry("CONFIRMED", "Payment received, order confirmed");
        orderRepository.save(order);
    }

    // Helper record
    private record OrderItemInfo(CartItem cartItem, CatalogProductInfo productInfo, Money itemTotal) {}

    // Request DTOs for REST calls
    private record ReserveStockRequest(UUID productId, UUID variantId, int quantity) {}
    private record DiscountRequest(String couponCode, BigDecimal subtotal, UUID customerId) {}
    private record ApplyCouponRequest(String couponCode, UUID customerId, UUID orderId) {}
}