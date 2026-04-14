package com.platform.order.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestDTO {

    @NotNull
    private UUID orderId;

    private UUID subOrderId;   // optional – can return whole order or specific sub-order

    @NotNull
    private UUID customerId;

    @NotBlank
    private String reason;

    private String description;
}