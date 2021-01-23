package one.microproject.iamservice.core.services.impl.caches;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class CacheHolderImpl<V> implements CacheHolder<V> {

    private final Map<String, V> cache;

    public CacheHolderImpl() {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public Set<String> keys() {
        return Set.copyOf(cache.keySet());
    }

    @Override
    public void put(String key, V value) {
        this.cache.put(key, value);
    }

    @Override
    public V get(String key) {
        return this.cache.get(key);
    }

    @Override
    public int size() {
        return this.cache.size();
    }

    @Override
    public V remove(String key) {
        return this.cache.remove(key);
    }

    @Override
    public void remove(Predicate<V> predicate) {
        Set<String> keysToRemove = new HashSet<>();
        this.cache.forEach((k,v) -> {
            if (predicate.test(v)) {
                keysToRemove.add(k);
            }
        });
        keysToRemove.forEach(cache::remove);
    }

}
