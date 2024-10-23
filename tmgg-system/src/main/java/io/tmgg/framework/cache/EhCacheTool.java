package io.tmgg.framework.cache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.io.File;


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

    public static <K, V> Cache<K, V> create(String name, Class<K> k, Class<V> v, int heap) {
        return getInstance().createCache(name,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                k,
                                v,
                                ResourcePoolsBuilder.heap(heap)
                        )

                        .build());


//        cacheManager.close();

    }

}
