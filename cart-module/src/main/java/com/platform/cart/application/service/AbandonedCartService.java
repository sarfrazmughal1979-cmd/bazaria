package com.platform.cart.application.service;
import com.platform.cart.domain.model.Cart;
import com.platform.cart.domain.model.CartStatus;
import com.platform.cart.domain.repository.CartRepository;
import com.platform.core.client.ResilientRestClient;
import com.platform.core.client.RestClientFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbandonedCartService {
    private final CartRepository cartRepository;
    private final RestClientFactory restClientFactory;
    @Value("${module.notification.url:http://localhost:8080}")
    private String notificationBaseUrl;
    private ResilientRestClient notificationRestClient;
    @PostConstruct
    public void init() { notificationRestClient = restClientFactory.create(notificationBaseUrl, 10); }

    @Scheduled(cron = "0 0 */2 * * *")
    public void detectAbandonedCarts() {
        Instant twoHoursAgo = Instant.now().minus(2, ChronoUnit.HOURS);
        List<Cart> abandoned = cartRepository.findByStatusAndUpdatedAtBefore(CartStatus.ACTIVE, twoHoursAgo);
        for (Cart cart : abandoned) {
            if (cart.getCustomerId() != null) {
                notificationRestClient.post("/api/internal/notifications/send", Map.of("userId", cart.getCustomerId(), "templateKey", "CART_ABANDONED"), Void.class);
            }
        }
    }
}