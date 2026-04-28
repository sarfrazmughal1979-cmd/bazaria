package com.platform.loyalty.application.listener;

import com.platform.common.domain.event.OrderPlacedEvent;
import com.platform.loyalty.application.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final LoyaltyService loyaltyService;

    @Async
    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        loyaltyService.earnPoints(UUID.fromString(event.getCustomerId()),
                event.getTotalAmount(),
                UUID.fromString(event.getOrderId()));
    }
}