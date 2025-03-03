package io.tmgg.framework.cache;

import io.tmgg.SysProp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.io.File;

@Slf4j
@Configuration
public class EhCacheConfig {

    @Resource
    private SysProp prop ;

    @Bean
    public CacheManager ehCacheManager() {
        String cacheDir = prop.getCacheDir();
        log.info("系统缓存目录 {}",cacheDir);
        Assert.hasText(cacheDir, "缓存目录不能为空");
        CacheManager cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(new File(cacheDir)))
                .build(true);

        return cacheManager;
    }

}
