package com.platform.core.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper mapper = JsonMapper.builder()
                        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                        .disable(SerializationFeature.FAIL_ON_ORDER_MAP_BY_INCOMPARABLE_KEY)
                        .disable(SerializationFeature.FAIL_ON_SELF_REFERENCES)
                        .disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS)
                        .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJacksonJsonRedisSerializer(mapper))
                )
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("products", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigs.put("categories", defaultConfig.entryTtl(Duration.ofHours(6)));
        cacheConfigs.put("brands", defaultConfig.entryTtl(Duration.ofHours(6)));
        cacheConfigs.put("banners", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigs.put("vendors", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigs.put("cart", defaultConfig.entryTtl(Duration.ofDays(7)));
        cacheConfigs.put("flash-sales", defaultConfig.entryTtl(Duration.ofMinutes(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .transactionAware()
                .build();
    }
}