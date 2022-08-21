package ru.job4j.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCache<K, V> {
    protected final Map<K, SoftReference<V>> cache = new HashMap<>();

    public void put(K key, V value) {
        SoftReference<V> soft = new SoftReference<>(value);
        cache.put(key, soft);
    }

    public V get(K key) {
        V rsl;
        if (cache.get(key) != null) {
            rsl = cache.get(key).get();
        } else {
            rsl = load(key);
            put(key, rsl);
        }
        return rsl;
    }

    protected abstract V load(K key);
}
