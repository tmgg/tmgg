package io.tmgg.framework.cache;

import io.tmgg.SysProp;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.time.Duration;

/**
 * 使用ehcache的主要原因是可以缓存到硬盘
 */
@Slf4j
@Component
public class EhCacheService implements CacheService {


    private CacheManager cacheManager;


    @Resource
    private SysProp prop ;

    @PostConstruct
    public void init() {
        String cacheDir = prop.getCacheDir();
        log.info("系统缓存目录 {}",cacheDir);
        Assert.hasText(cacheDir, "缓存目录不能为空");
        cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(new File(cacheDir)))
                .build(true);
    }


    /**
     * @param name
     * @param k
     * @param v
     * @param heap 对象数量
     */
    @Override
    public <K, V> Cache<K, V> create(String name, Class<K> k, Class<V> v, int maxCount, Duration timeToIdleExpiration) {
        CacheConfigurationBuilder<K, V> cfg = CacheConfigurationBuilder.newCacheConfigurationBuilder(
                k,
                v,
                ResourcePoolsBuilder.heap(maxCount)
        );

        cfg.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(timeToIdleExpiration));

        return cacheManager.createCache(name, cfg.build());
    }

    /**
     * 轻量的cache， 默认1000个记录，5分钟失效， 基于内存
     *
     * @param name
     * @return
     */
    @Override
    public Cache<String, String> createLight(String name) {
        CacheConfigurationBuilder<String, String> cfg = CacheConfigurationBuilder.newCacheConfigurationBuilder(
                String.class,
                String.class,
                ResourcePoolsBuilder.heap(1000)
        );

        cfg.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMinutes(5)));

        return cacheManager.createCache(name, cfg.build());
    }

    /**
     * 持久化到硬盘的缓存
     *
     * @param name
     * @param k
     * @param v
     * @param maxCount
     * @param maxSizeMb
     * @param <K>
     * @param <V>
     * @return
     */
    @Override
    public <K, V> Cache<K, V> createPersistent(String name, Class<K> k, Class<V> v, int maxCount, int maxSizeMb) {
        CacheConfigurationBuilder<K, V> cfg = CacheConfigurationBuilder.newCacheConfigurationBuilder(
                k,
                v,
                ResourcePoolsBuilder.heap(maxCount).disk(maxSizeMb, MemoryUnit.MB, true));

        return cacheManager.createCache(name, cfg);
    }
}