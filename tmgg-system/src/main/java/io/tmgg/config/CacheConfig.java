
package io.tmgg.config;

import io.tmgg.core.cache.ResourceCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 缓存的配置，默认使用基于内存的缓存，如果分布式部署请更换为redis
 *
 */
@Configuration
public class CacheConfig {

    /**
     * url资源的缓存，默认不过期
     *
     */
    @Bean
    public ResourceCache resourceCache() {
        return new ResourceCache();
    }





}
