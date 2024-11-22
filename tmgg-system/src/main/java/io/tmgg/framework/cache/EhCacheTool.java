package io.tmgg.framework.cache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.io.File;
import java.time.Duration;


public class EhCacheTool {
    private static CacheManager INSTANCE;

    public static synchronized CacheManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = CacheManagerBuilder
                    .newCacheManagerBuilder()
                    .with(CacheManagerBuilder.persistence(new File("/tmgg/cache")))
                    .build(true);


        }
        return INSTANCE;
    }

    /**
     * @param name
     * @param k
     * @param v
     * @param heap 对象数量
     */
    public static <K, V> Cache<K, V> create(String name, Class<K> k, Class<V> v, int maxCount, Duration timeToIdleExpiration) {
        CacheConfigurationBuilder<K, V> cfg = CacheConfigurationBuilder.newCacheConfigurationBuilder(
                k,
                v,
                ResourcePoolsBuilder.heap(maxCount)
        );

        cfg.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(timeToIdleExpiration));

        return getInstance().createCache(name, cfg.build());

    }

}
