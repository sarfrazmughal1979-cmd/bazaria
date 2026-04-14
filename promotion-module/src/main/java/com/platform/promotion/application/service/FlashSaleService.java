package com.platform.promotion.application.service;

import com.platform.core.domain.Money;
import com.platform.core.event.DomainEventPublisher;
import com.platform.core.exception.BusinessException;
import com.platform.promotion.application.dto.*;
import com.platform.promotion.domain.event.FlashSaleStartedEvent;
import com.platform.promotion.domain.model.*;
import com.platform.promotion.domain.repository.FlashSaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlashSaleService{

    private final FlashSaleRepository flashSaleRepository;
    private final DomainEventPublisher eventPublisher;
    private final RedissonClient redissonClient;

    public Optional<FlashSale> findById(UUID flashSaleId){
        return flashSaleRepository.findById(flashSaleId);
    }
    @Transactional
    public FlashSale createFlashSale(FlashSaleRequest request) {
        FlashSale flashSale = FlashSale.builder()
                .name(request.getName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(false)
                .build();

        for (var itemReq : request.getItems()) {
            FlashSaleItem item = FlashSaleItem.builder()
                    .flashSale(flashSale)
                    .productId(itemReq.getProductId())
                    .flashSalePrice(Money.of(itemReq.getFlashSalePrice(),
                            itemReq.getCurrency()))
                    .totalQuantity(itemReq.getTotalQuantity())
                    .soldQuantity(0)
                    .limitPerCustomer(itemReq.getLimitPerCustomer())
                    .build();
            flashSale.getItems().add(item);
        }

        FlashSale saved = flashSaleRepository.save(flashSale);

        // Pre-load quantities into Redis for fast access
        for (FlashSaleItem item : saved.getItems()) {
            String key = "flash_sale:" + saved.getId() + ":product:" + item.getProductId();
            RAtomicLong stock = redissonClient.getAtomicLong(key);
            stock.set(item.getTotalQuantity());
        }

        return saved;
    }

    public boolean attemptFlashSalePurchase(UUID flashSaleId, UUID productId,
                                             UUID customerId, int quantity) {
        String stockKey = "flash_sale:" + flashSaleId + ":product:" + productId;
        String customerKey = stockKey + ":customer:" + customerId;

        RAtomicLong stock = redissonClient.getAtomicLong(stockKey);
        RAtomicLong customerPurchased = redissonClient.getAtomicLong(customerKey);

        // Check customer limit (e.g., max 2 per customer)
        if (customerPurchased.get() + quantity > 2) {
            throw new BusinessException("FLASH_SALE_LIMIT",
                    "Exceeded maximum purchase limit");
        }

        // Atomically decrement stock
        long remaining = stock.addAndGet(-quantity);
        if (remaining < 0) {
            stock.addAndGet(quantity); // Restore
            throw new BusinessException("FLASH_SALE_SOLD_OUT",
                    "Flash sale item is sold out");
        }

        customerPurchased.addAndGet(quantity);
        return true;
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    @Transactional
    public void activateFlashSales() {
        Instant now = Instant.now();
        List<FlashSale> toActivate = flashSaleRepository
                .findByIsActiveFalseAndStartTimeLessThanEqual(now);

        for (FlashSale flashSale : toActivate) {
            flashSale.setActive(true);
            flashSaleRepository.save(flashSale);
            eventPublisher.publish(new FlashSaleStartedEvent(
                    flashSale.getId().toString(), flashSale.getName()));
        }

        // Deactivate expired
        List<FlashSale> toDeactivate = flashSaleRepository
                .findByIsActiveTrueAndEndTimeLessThanEqual(now);
        for (FlashSale flashSale : toDeactivate) {
            flashSale.setActive(false);
            flashSaleRepository.save(flashSale);
        }
    }
}