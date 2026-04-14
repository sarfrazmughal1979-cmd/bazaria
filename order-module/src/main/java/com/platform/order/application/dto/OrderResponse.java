package com.platform.order.application.dto;

import com.platform.core.domain.Address;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private UUID orderId;
    private String orderNumber;
    private UUID customerId;
    private String status;

    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String currency;

    private String couponCode;
    private String paymentMethod;
    private UUID paymentId;
    private Address shippingAddress;
    private String customerNote;

    private List<SubOrderResponse> subOrders;
    private List<OrderTimelineResponse> timeline;

    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private UUID productId;
        private UUID variantId;
        private String productName;
        private String productImage;
        private String sku;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderTimelineResponse {
        private String status;
        private String description;
        private Instant createdAt;
    }
}