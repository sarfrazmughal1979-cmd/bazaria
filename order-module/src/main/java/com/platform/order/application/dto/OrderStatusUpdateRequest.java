package com.platform.order.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;          // e.g., "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"

    private String trackingNumber;  // required when status = SHIPPED

    private String reason;          // required when status = CANCELLED
}