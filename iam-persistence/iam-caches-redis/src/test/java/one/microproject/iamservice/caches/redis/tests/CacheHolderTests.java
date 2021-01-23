package one.microproject.iamservice.caches.redis.tests;

import one.microproject.iamservice.caches.redis.RedisCacheHolderImpl;
import one.microproject.iamservice.caches.redis.RedisConfiguration;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.services.impl.caches.CacheHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CacheHolderTests {

    private static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6-alpine")
            .withExposedPorts(6379)
            .withReuse(true);

    private static CacheHolder<JWToken> cacheHolder;

    @BeforeAll
    public static void init() {
        redisContainer.start();
        Assertions.assertTrue(redisContainer.isRunning());
        String host = redisContainer.getContainerIpAddress();
        int port = redisContainer.getMappedPort(6379);
        cacheHolder = new RedisCacheHolderImpl<>(new RedisConfiguration(host, port), JWToken.class);
    }

    @Test
    @Order(0)
    void initTest() {
        Set<String> keys = cacheHolder.keys();
        assertNotNull(keys);
        assertEquals(0, keys.size());
    }

    @Test
    @Order(1)
    void insertDataTest() {
        cacheHolder.put("001", JWToken.from("data-001"));
        cacheHolder.put("002", JWToken.from("data-002"));
        Set<String> keys = cacheHolder.keys();
        assertNotNull(keys);
        assertEquals(2, keys.size());
        assertEquals(2, cacheHolder.size());
        assertNotNull(cacheHolder.get("001"));
        assertNotNull(cacheHolder.get("002"));
        assertNull(cacheHolder.get("003"));
    }

    @Test
    @Order(3)
    void deleteDataTest() {
        JWToken removed = cacheHolder.remove("001");
        assertNotNull(removed);
        removed = cacheHolder.remove("001");
        assertNull(removed);
        removed = cacheHolder.remove("002");
        assertNotNull(removed);
        removed = cacheHolder.remove("002");
        assertNull(removed);
    }

    @Test
    @Order(4)
    void lastTest() {
        Set<String> keys = cacheHolder.keys();
        assertNotNull(keys);
        assertEquals(0, keys.size());
    }

}
