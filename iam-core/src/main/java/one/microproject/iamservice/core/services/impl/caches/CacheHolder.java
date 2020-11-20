package one.microproject.iamservice.core.services.impl.caches;

import java.util.Set;
import java.util.function.Predicate;

public interface CacheHolder<V> {

    Set<String> keys();

    void put(String key, V value);

    V get(String key);

    int size();

    V remove(String key);

    void remove(Predicate<V> predicate);

}
