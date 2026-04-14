package com.platform.order.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubOrderResponse {

    private UUID subOrderId;
    private String subOrderNumber;
    private UUID orderId;
    private UUID vendorId;
    private String status;

    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal commissionAmount;

    private UUID shipmentId;
    private String trackingNumber;

    private List<OrderResponse.OrderItemResponse> items;

    private Instant createdAt;
}