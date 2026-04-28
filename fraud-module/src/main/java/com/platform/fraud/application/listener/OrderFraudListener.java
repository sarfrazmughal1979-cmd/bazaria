package com.platform.fraud.application.listener;

import com.platform.common.domain.event.OrderPlacedEvent;
import com.platform.fraud.application.service.FraudService;
import com.platform.fraud.domain.model.FraudCheck;
import com.platform.fraud.domain.repository.FraudCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderFraudListener {

    private final FraudService fraudService;

    @Async
    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        fraudService.evaluateOrder(
                UUID.fromString(event.getOrderId()),
                UUID.fromString(event.getCustomerId()),
                event.getTotalAmount().doubleValue(),
                event.getCustomerIp()  // placeholder IP
        );
    }
}