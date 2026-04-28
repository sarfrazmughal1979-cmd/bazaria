package com.platform.loyalty.application.service;
import com.platform.loyalty.domain.repository.LoyaltyAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class LoyaltyExpirationScheduler {
    private final LoyaltyAccountRepository accountRepository;
    @Value("${platform.loyalty.expiration-months:12}")
    private int expirationMonths;
    @Scheduled(cron = "0 0 3 1 * *")
    @Transactional
    public void expirePoints() {
        Instant cutoff = Instant.now().minus(expirationMonths, ChronoUnit.MONTHS);
        System.out.println("Checking points before " + cutoff);
    }
}