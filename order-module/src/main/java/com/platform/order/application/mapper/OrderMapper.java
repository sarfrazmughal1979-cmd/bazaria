package com.platform.order.application.mapper;

import com.platform.core.domain.Address;
import com.platform.core.domain.Money;
import com.platform.order.application.dto.OrderResponse;
import com.platform.order.application.dto.PlaceOrderRequest;
import com.platform.order.application.dto.SubOrderResponse;
import com.platform.order.domain.model.Order;
import com.platform.order.domain.model.OrderItem;
import com.platform.order.domain.model.OrderTimeline;
import com.platform.order.domain.model.SubOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    // ============================================================
    // Request to embedded objects
    // ============================================================

    public Address toAddress(PlaceOrderRequest.ShippingAddressRequest request) {
        if (request == null) return null;
        return Address.builder()
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .build();
    }

    // ============================================================
    // Entity to Response DTOs
    // ============================================================

    public OrderResponse toResponse(Order order) {
        if (order == null) return null;

        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .subtotal(getAmount(order.getSubtotal()))
                .shippingCost(getAmount(order.getShippingCost()))
                .discountAmount(getAmount(order.getDiscountAmount()))
                .taxAmount(getAmount(order.getTaxAmount()))
                .totalAmount(getAmount(order.getTotalAmount()))
                .currency(getCurrency(order.getTotalAmount()))
                .couponCode(order.getCouponCode())
                .paymentMethod(order.getPaymentMethod())
                .paymentId(order.getPaymentId())
                .shippingAddress(order.getShippingAddress())
                .customerNote(order.getCustomerNote())
                .subOrders(mapSubOrders(order.getSubOrders()))
                .timeline(mapTimeline(order.getTimeline())) // if timeline DTO is needed, map separately
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
    // Add this helper method
    private List<OrderResponse.OrderTimelineResponse> mapTimeline(List<OrderTimeline> timeline) {
        if (timeline == null) return List.of();
        return timeline.stream()
                .map(t -> OrderResponse.OrderTimelineResponse.builder()
                        .status(t.getStatus())
                        .description(t.getDescription())
                        .createdAt(t.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    public SubOrderResponse toSubOrderResponse(SubOrder subOrder) {
        if (subOrder == null) return null;

        return SubOrderResponse.builder()
                .subOrderId(subOrder.getId())
                .subOrderNumber(subOrder.getSubOrderNumber())
                .orderId(subOrder.getOrder() != null ? subOrder.getOrder().getId() : null)
                .vendorId(subOrder.getVendorId())
                .status(subOrder.getStatus() != null ? subOrder.getStatus().name() : null)
                .subtotal(getAmount(subOrder.getSubtotal()))
                .shippingCost(getAmount(subOrder.getShippingCost()))
                .commissionAmount(getAmount(subOrder.getCommissionAmount()))
                .shipmentId(subOrder.getShipmentId())
                .trackingNumber(subOrder.getTrackingNumber())
                .items(mapOrderItems(subOrder.getItems()))
                .createdAt(subOrder.getCreatedAt())
                .build();
    }

    // ============================================================
    // Helper methods for Money extraction
    // ============================================================

    private BigDecimal getAmount(Money money) {
        return money != null ? money.getAmount() : BigDecimal.ZERO;
    }

    private String getCurrency(Money money) {
        return money != null ? money.getCurrencyCode() : "BDT";
    }

    // ============================================================
    // Collection mappings
    // ============================================================

    private List<SubOrderResponse> mapSubOrders(List<SubOrder> subOrders) {
        if (subOrders == null) return List.of();
        return subOrders.stream()
                .map(this::toSubOrderResponse)
                .collect(Collectors.toList());
    }

    private List<OrderResponse.OrderItemResponse> mapOrderItems(List<OrderItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem item) {
        if (item == null) return null;

        return OrderResponse.OrderItemResponse.builder()
                .productId(item.getProductId())
                .variantId(item.getVariantId())
                .productName(item.getProductName())
                .productImage(item.getProductImage())
                .sku(item.getSku())
                .quantity(item.getQuantity())
                .unitPrice(getAmount(item.getUnitPrice()))
                .totalPrice(getAmount(item.getTotalPrice()))
                .build();
    }
}