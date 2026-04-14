package com.platform.inventory.application.service;

import com.platform.core.event.DomainEventPublisher;
import com.platform.core.exception.BusinessException;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.inventory.application.dto.*;
import com.platform.inventory.domain.event.StockDepletedEvent;
import com.platform.inventory.domain.event.StockReservedEvent;
import com.platform.inventory.domain.model.*;
import com.platform.inventory.domain.repository.InventoryRepository;
import com.platform.inventory.domain.repository.StockMovementRepository;
import com.platform.inventory.domain.repository.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockReservationRepository reservationRepository;
    private final DomainEventPublisher eventPublisher;
    private final RedissonClient redissonClient;

    @Transactional
    public void releaseExpiredReservations() {
        List<StockReservation> expiredReservations = reservationRepository
                .findByStatusAndExpiresAtBefore("ACTIVE", Instant.now());
        for (StockReservation reservation : expiredReservations) {
            releaseReservation(reservation.getId());
        }
        log.info("Released {} expired reservations", expiredReservations.size());
    }
    @Transactional
    public UUID reserveStock(UUID productId, UUID variantId, int quantity) {
        String lockKey = "inventory:" + productId + ":" + (variantId != null ? variantId : "default");
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                throw new BusinessException("LOCK_TIMEOUT",
                        "Could not acquire inventory lock");
            }

            InventoryItem item = inventoryRepository
                    .findByProductIdAndVariantId(productId, variantId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Inventory", "productId", productId));

            if (item.getAvailableQuantity() < quantity) {
                throw new BusinessException("INSUFFICIENT_STOCK",
                        String.format("Only %d items available", item.getAvailableQuantity()));
            }

            // Reserve stock
            item.setReservedQuantity(item.getReservedQuantity() + quantity);

            StockReservation reservation = StockReservation.builder()
                    .inventoryItem(item)
                    .quantity(quantity)
                    .status("ACTIVE")
                    .expiresAt(Instant.now().plusSeconds(1800)) // 30 min expiry
                    .build();

            item.getReservations().add(reservation);
            inventoryRepository.save(item);

            // Record movement
            StockMovement movement = StockMovement.builder()
                    .inventoryItem(item)
                    .type(MovementType.RESERVATION)
                    .quantity(-quantity)
                    .reason("Stock reserved for order")
                    .build();
            stockMovementRepository.save(movement);

            eventPublisher.publish(new StockReservedEvent(
                    productId.toString(), quantity,reservation.getId().toString()));

            // Check low stock
            if (item.getAvailableQuantity() <= item.getReorderPoint()) {
                eventPublisher.publish(new StockDepletedEvent(
                        productId.toString(),
                        item.getAvailableQuantity(),0));
            }

            return reservation.getId();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("RESERVATION_ERROR",
                    "Stock reservation was interrupted");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public void confirmReservation(UUID reservationId) {
        // Deduct from actual stock when order is confirmed
        // Implementation similar to reserveStock with different movement type
    }

    @Transactional
    public void releaseReservation(UUID reservationId) {
        // Release reserved stock back
        // Used for order cancellation or reservation timeout
    }

    @Transactional
    public void updateStock(UUID productId, UUID variantId, StockUpdateRequest request) {
        InventoryItem item = inventoryRepository
                .findByProductIdAndVariantId(productId, variantId)
                .orElseGet(() -> InventoryItem.builder()
                        .productId(productId)
                        .variantId(variantId)
                        .quantity(0)
                        .reservedQuantity(0)
                        .reorderPoint(request.getReorderPoint() != null ? 
                            request.getReorderPoint() : 10)
                        .build());

        int previousQty = item.getQuantity();
        item.setQuantity(request.getQuantity());
        
        if (request.getReorderPoint() != null) {
            item.setReorderPoint(request.getReorderPoint());
        }

        inventoryRepository.save(item);

        StockMovement movement = StockMovement.builder()
                .inventoryItem(item)
                .type(MovementType.ADJUSTMENT)
                .quantity(request.getQuantity() - previousQty)
                .reason(request.getReason())
                .build();
        stockMovementRepository.save(movement);
    }


    public int getAvailableStock(UUID productId, UUID variantId) {
        return inventoryRepository.findByProductIdAndVariantId(productId, variantId)
                .map(InventoryItem::getAvailableQuantity)
                .orElse(0);
    }
}