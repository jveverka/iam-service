package one.microproject.iamservice.caches.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.services.impl.caches.CacheHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.function.Predicate;

public class RedisCacheHolderImpl<V> implements CacheHolder<V> {

    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheHolderImpl.class);

    private final String keyPrefix;
    private final Jedis jedis;
    private final ObjectMapper mapper;
    private final Class<V> type;

    public RedisCacheHolderImpl(RedisConfiguration configuration, Class<V> type) {
        this.keyPrefix = type.getCanonicalName();
        this.jedis = new Jedis(configuration.getHostname(), configuration.getPort());
        this.mapper = new ObjectMapper();
        this.type = type;
    }

    @Override
    public Set<String> keys() {
        LOG.trace("keys");
        return jedis.keys(keyPrefix + ":*");
    }

    @Override
    public void put(String key, V value) throws CacheReadException {
        try {
            LOG.trace("put {}:{}", key, value.toString());
            jedis.set(keyPrefix + ":" + key, mapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new CacheReadException(e);
        }
    }

    @Override
    public V get(String key) throws CacheReadException {
        try {
            LOG.trace("get {}", key);
            String value = jedis.get(keyPrefix + ":" + key);
            if (value != null) {
                return mapper.readValue(value, type);
            } else {
                return null;
            }
        } catch (JsonProcessingException e) {
            throw new CacheReadException(e);
        }
    }

    @Override
    public int size() {
        return jedis.keys(keyPrefix + ":*").size();
    }

    @Override
    public V remove(String key) throws CacheReadException {
        try {
            LOG.trace("remove {}", key);
            String value = jedis.get(keyPrefix + ":" + key);
            if (value != null) {
                V v = mapper.readValue(value, type);
                jedis.del(keyPrefix + ":" + key);
                return v;
            } else {
                return null;
            }
        } catch (JsonProcessingException e) {
            throw new CacheReadException(e);
        }
    }

    @Override
    public void remove(Predicate predicate) throws CacheReadException {
        try {
            LOG.trace("remove by predicate");
            Set<String> keys = jedis.keys(keyPrefix + ":*");
            for (String key: keys) {
                String value = jedis.get(key);
                if (value != null) {
                    V v = mapper.readValue(value, type);
                    if (predicate.test(v)) {
                        jedis.del(key);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new CacheReadException(e);
        }
    }

}
