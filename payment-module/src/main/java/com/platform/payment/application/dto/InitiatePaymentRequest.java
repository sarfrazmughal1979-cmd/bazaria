package com.platform.payment.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Payment gateway is required")
    private String gateway;        // e.g., "STRIPE", "BKASH"

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;  // e.g., "CREDIT_CARD", "MOBILE_BANKING"

    private String successUrl;     // optional – where to redirect after success
    private String cancelUrl;      // optional – where to redirect after cancellation
}