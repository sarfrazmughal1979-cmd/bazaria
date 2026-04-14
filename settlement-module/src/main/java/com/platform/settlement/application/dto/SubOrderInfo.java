package com.platform.settlement.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SubOrderInfo(
        UUID subOrderId,
        UUID orderId,
        UUID vendorId,
        UUID categoryId,
        BigDecimal subtotal,
        String status
) {}