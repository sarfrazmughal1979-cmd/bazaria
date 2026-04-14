package com.platform.core.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private final StringRedisTemplate redisTemplate;
    public boolean isProcessed(String idempotencyKey) {
        return redisTemplate.hasKey(idempotencyKey);
    }
    public void markProcessed(String idempotencyKey, Duration ttl) {
        redisTemplate.opsForValue().set(idempotencyKey, "processed", ttl);
    }
}
