package io.tmgg.framework.cache;

import org.ehcache.Cache;

import java.time.Duration;

public interface CacheService {


    <K, V> Cache<K, V> create(String name, Class<K> k, Class<V> v, int maxCount, Duration timeToIdleExpiration);

    Cache<String, String> createLight(String name);

    <K, V> Cache<K, V> createPersistent(String name, Class<K> k, Class<V> v, int maxCount, int maxSizeMb);
}
