package com.platform.order.api;

import com.platform.common.domain.event.OrderDeliveredEvent;
import com.platform.core.dto.ApiResponse;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.security.SecurityUtils;
import com.platform.order.application.dto.OrderResponse;
import com.platform.order.application.dto.PlaceOrderRequest;
import com.platform.order.application.service.OrderService;
import com.platform.order.domain.model.Order;
import com.platform.order.domain.model.OrderStatus;
import com.platform.order.domain.model.SubOrder;
import com.platform.order.domain.model.SubOrderStatus;
import com.platform.order.domain.repository.OrderRepository;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.order.domain.repository.SubOrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final SubOrderRepository subOrderRepository;

    private final OrderService orderService;

    private final DomainEventPublisher eventPublisher;

    @GetMapping("/{orderId}/info-mini")
    public ResponseEntity<OrderInfoMini> getOrderInfoMini(@PathVariable UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return ResponseEntity.ok(new OrderInfoMini(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrencyCode(),
                order.getStatus().name()
        ));
    }

    public record OrderInfoMini(UUID orderId, String orderNumber, UUID customerId,
                                BigDecimal totalAmount, String currency, String status) {}

    // In OrderController or a separate internal controller

    @GetMapping("/sub-orders/{subOrderId}/info-mini")
    public ResponseEntity<SubOrderInfo> getSubOrderInfoMini(@PathVariable UUID subOrderId) {
        SubOrder subOrder = subOrderRepository.findById(subOrderId)
            .orElseThrow(() -> new ResourceNotFoundException("SubOrder", "id", subOrderId));
        return ResponseEntity.ok(new SubOrderInfo(
                subOrder.getId(), subOrder.getOrder().getId(), subOrder.getVendorId(),
            subOrder.getStatus().name(),
            subOrder.getSubtotal().getAmount()
    ));
    }
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam(required = false) String reason) {
        orderService.cancelOrder(orderId, reason != null ? reason : "Cancelled by user");
        return ResponseEntity.ok(ApiResponse.success("Order cancelled"));
    }
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmOrder(
            @PathVariable UUID orderId,
            @RequestParam UUID paymentId) {
        orderService.confirmOrder(orderId, paymentId);
        return ResponseEntity.ok(ApiResponse.success("Order confirmed"));
    }
    @GetMapping("/sub-orders/delivered")
    public ResponseEntity<List<SubOrderInfo>> getDeliveredSubOrdersBetween(
            @RequestParam Instant start,
            @RequestParam Instant end) {

        List<SubOrder> subOrders = subOrderRepository.findByStatusAndDeliveredAtBetween(
                SubOrderStatus.DELIVERED, start, end);

        List<SubOrderInfo> result = subOrders.stream()
                .map(so -> new SubOrderInfo(
                        so.getId(),
                        so.getOrder().getId(),
                        so.getVendorId(),
						so.getStatus().name(),
                        so.getSubtotal().getAmount()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/sub-orders/{subOrderId}/assign-shipment")
    public ResponseEntity<Void> assignShipmentToSubOrder(
            @PathVariable UUID subOrderId,
            @RequestBody AssignShipmentRequest request) {
        SubOrder subOrder = subOrderRepository.findById(subOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("SubOrder", "id", subOrderId));
        subOrder.setShipmentId(request.shipmentId());
        subOrder.setTrackingNumber(request.trackingNumber());
        subOrder.setStatus(SubOrderStatus.SHIPPED);
        subOrderRepository.save(subOrder);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sub-orders/{subOrderId}/mark-delivered")
    public ResponseEntity<Void> markSubOrderDelivered(@PathVariable UUID subOrderId) {
        SubOrder subOrder = subOrderRepository.findById(subOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("SubOrder", "id", subOrderId));
        subOrder.markAsDelivered();
        subOrderRepository.save(subOrder);
        // Check if all sub-orders are delivered to update main order status
        Order order = subOrder.getOrder();
        boolean allDelivered = order.getSubOrders().stream()
                .allMatch(so -> so.getStatus() == SubOrderStatus.DELIVERED);
        if (allDelivered) {
            order.setStatus(OrderStatus.DELIVERED);
            eventPublisher.publish(new OrderDeliveredEvent(
                    order.getId().toString(), order.getOrderNumber(), order.getCustomerId().toString()
            ));
            orderRepository.save(order);
        }
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{orderId}/detail")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return ResponseEntity.ok(buildOrderDetail(order));
    }
    @PostMapping("/placeOrder")
    @Operation(summary = "Place a new order")
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        OrderResponse response = orderService.placeOrder(request, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    private OrderDetailResponse buildOrderDetail(Order order) {
        List<SubOrderDetailResponse> subDetails = order.getSubOrders().stream()
                .map(so -> new SubOrderDetailResponse(
                        so.getVendorId(),
                        so.getItems().stream()
                                .map(item -> new OrderItemDetailResponse(
                                        item.getProductId(),
                                        item.getQuantity(),
                                        item.getTotalPrice().getAmount()))
                                .toList()
                ))
                .toList();
        return new OrderDetailResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getTotalAmount().getAmount(),
                subDetails
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Order>>> getMyOrders(@PageableDefault(size=20) Pageable pageable) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Page<Order> orders = orderRepository.findByCustomerId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    // DTO records (add inside OrderController)
    public record OrderDetailResponse(
            UUID orderId,
            String orderNumber,
            UUID customerId,
            BigDecimal totalAmount,
            List<SubOrderDetailResponse> subOrders
    ) {}

    public record SubOrderDetailResponse(UUID vendorId, List<OrderItemDetailResponse> items) {}
    public record OrderItemDetailResponse(UUID productId, int quantity, BigDecimal totalPrice) {}
    public record OrderInfo(UUID orderId, String orderNumber, UUID customerId, UUID vendorId,
                            BigDecimal totalAmount, String currency, String status) {}
    public record SubOrderInfo(UUID subOrderId, UUID orderId, UUID vendorId, String status,
                               BigDecimal subtotal) {}
    public record AssignShipmentRequest(UUID shipmentId, String trackingNumber) {}
}