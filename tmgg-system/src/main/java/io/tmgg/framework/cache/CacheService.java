package io.tmgg.framework.cache;

import cn.hutool.cache.CacheUtil;
import cn.hutool.core.io.FileUtil;
import io.tmgg.SysProp;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

/**
 * 使用ehcache的主要原因是可以缓存到硬盘
 */
@Slf4j
@Component
public class CacheService implements Closeable {


    private final PersistentCacheManager cacheManager;


    public CacheService(SysProp prop) {
        String cacheDir = prop.getCacheDir();
        log.info("系统缓存目录 {}", cacheDir);
        Assert.hasText(cacheDir, "缓存目录未配置");

        this.cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(new File(cacheDir)))
                .build(true);
    }


    /***
     * 当配置了 多级存储（如 heap → disk），且内存（堆或堆外）容量不足时，Ehcache 会将淘汰的条目写入磁盘。
     * @param name
     * @param valueType
     * @param heapCount 堆内缓存个数. 相当于存在内存里的，超出这个数量，就存在磁盘上去了. 建议配置不宜过大，为预估常用个数即可, 例如在线用户，预估100人就填100。
     * @param diskSize
     * @param timeout
     * @return
     * @param <V>
     */
    public <V> Cache<String, V> newCache(String name, Class<V> valueType, int heapCount, int diskSize, Duration timeout) {
        Class<String> keyType = String.class;

        ResourcePoolsBuilder diskPool = ResourcePoolsBuilder
                .heap(heapCount)
                .disk(diskSize, MemoryUnit.MB, true);

        CacheConfigurationBuilder<String, V> cfg = CacheConfigurationBuilder.newCacheConfigurationBuilder(keyType, valueType, diskPool);
        cfg.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(timeout));


        Cache<String, V> cache = cacheManager.createCache(name, cfg);


        return cache;
    }



    public CacheManager getCacheManager() {
        return cacheManager;
    }


    // springboot 会在关闭时调用close
    @Override
    public void close() throws IOException {
        // 系统关闭时自动刷盘。
        cacheManager.close();
    }
}
