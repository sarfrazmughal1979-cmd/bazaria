package com.platform.loyalty.application.service;

import com.platform.loyalty.domain.model.LoyaltyAccount;
import com.platform.loyalty.domain.model.LoyaltyTransaction;
import com.platform.loyalty.domain.repository.LoyaltyAccountRepository;
import com.platform.loyalty.domain.repository.LoyaltyTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoyaltyExpirationScheduler {

    private final LoyaltyAccountRepository accountRepository;
    private final LoyaltyTransactionRepository transactionRepository;

    @Value("${platform.loyalty.expiration-months:12}")
    private int expirationMonths;

    /**
     * Runs on the 1st day of each month at 3:00 AM.
     * You can adjust the cron expression as needed.
     */
    @Scheduled(cron = "0 0 3 1 * *")
    @Transactional
    public void expirePoints() {
        Instant cutoff = Instant.now().minus(expirationMonths, ChronoUnit.MONTHS);
        log.info("Starting loyalty point expiration – cutoff date: {}", cutoff);

        int pageSize = 100;
        Page<LoyaltyAccount> accountPage;

        do {
            accountPage = accountRepository.findAll(PageRequest.of(0, pageSize));
            accountPage.getContent().forEach(account -> processAccount(account, cutoff));

            pageSize = accountPage.hasNext() ? pageSize : 0; // avoid infinite loop
        } while (!accountPage.isLast() && pageSize > 0);

        log.info("Loyalty point expiration completed.");
    }

    private void processAccount(LoyaltyAccount account, Instant cutoff) {
        UUID customerId = account.getCustomerId();

        // Sum of points earned within the expiration window
        Optional<Long> earnedInWindow = transactionRepository.sumPointsByCustomerAndTypeAndAfter(
                customerId, "EARN", cutoff);
        // Sum of points redeemed within the expiration window
        Optional<Long> redeemedInWindow = transactionRepository.sumPointsByCustomerAndTypeAndAfter(
                customerId, "REDEEM", cutoff);

        long netEffective = Math.max(0, earnedInWindow.orElse(0L) - redeemedInWindow.orElse(0L));

        long currentAvailable = account.getAvailablePoints();

        if (currentAvailable > netEffective) {
            long expiredPoints = currentAvailable - netEffective;

            account.setAvailablePoints(netEffective);
            accountRepository.save(account);

            // Record the expiration event
            LoyaltyTransaction expiryTransaction = LoyaltyTransaction.builder()
                    .customerId(customerId)
                    .points(-expiredPoints)
                    .type("EXPIRE")
                    .description(String.format(
                            "%d points expired (older than %d months).", expiredPoints, expirationMonths))
                    .build();
            transactionRepository.save(expiryTransaction);

            log.info("Expired {} points for customer {}. Remaining: {}",
                    expiredPoints, customerId, netEffective);
        }
    }
}