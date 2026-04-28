package com.platform.cart.application.listener;

import com.platform.cart.application.service.CartMergeService;
import com.platform.common.domain.event.UserLoggedInEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLoginListener {
    private final CartMergeService cartMergeService;

    @Async
    @EventListener
    public void onUserLoggedIn(UserLoggedInEvent event) {
        UUID userId = UUID.fromString(event.getUserId());
        String sessionId = event.getSessionId();
        log.info("Cart merge triggered for user {} with session {}", userId, sessionId);
        cartMergeService.mergeGuestCart(userId, sessionId);
    }
}