package io.tmgg.framework.cache;

import io.tmgg.framework.session.SysHttpSession;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.stereotype.Component;

import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;

@Component
public class CacheConfig implements JCacheManagerCustomizer {

    @Override
    public void customize(CacheManager cacheManager) {
        MutableConfiguration<String, SysHttpSession> cfg = new MutableConfiguration<>();
        cacheManager.createCache("sysSession", cfg);
    }
}
