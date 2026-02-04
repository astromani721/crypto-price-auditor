package com.example.dockerpoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

class CacheConfigTest {

    @Test
    void redisCacheManager_appliesTtlAndCacheNames() {
        CacheConfig config = new CacheConfig();
        RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);
        Duration ttl = Duration.ofSeconds(15);

        RedisCacheManager manager = config.redisCacheManager(connectionFactory, ttl);

        assertTrue(manager.getCache("coinbaseSpot") != null);
        assertTrue(manager.getCache("coinbaseSpotReadonly") != null);

        Map<String, RedisCacheConfiguration> configs = manager.getCacheConfigurations();
        RedisCacheConfiguration spotConfig = configs.get("coinbaseSpot");
        RedisCacheConfiguration readonlyConfig = configs.get("coinbaseSpotReadonly");
        assertEquals(ttl, spotConfig.getTtl());
        assertEquals(ttl, readonlyConfig.getTtl());
    }
}
