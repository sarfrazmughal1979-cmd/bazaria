package com.platform.inventory.infrastructure.scheduler;

import com.platform.inventory.application.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryScheduler {

    private final InventoryService inventoryService;

    @Scheduled(fixedDelay = 300000) // every 5 minutes
    public void releaseExpiredReservations() {
        log.info("Running expired reservations cleanup");
        inventoryService.releaseExpiredReservations();
    }
}
