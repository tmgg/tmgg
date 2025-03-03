package io.tmgg.framework.session.config;

import io.tmgg.framework.cache.EhCacheService;
import io.tmgg.framework.session.SysHttpSession;
import org.ehcache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.HttpSessionIdResolver;

@Configuration
@EnableSpringHttpSession
public class MySessionConfig {

    @Bean
    public MySessionRepository sessionRepository() {
        return new MySessionRepository();
    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return new MyHttpSessionIdResolver();
    }


    @Bean("httpSessionCache")
    public Cache<String, SysHttpSession> httpSessionCache(EhCacheService cacheService) {
        Class<String> key = String.class;
        Class<SysHttpSession> value = SysHttpSession.class;
        String name = "httpSession";
        int maxCount = 1000;
        int maxSizeMb = 10;
        return cacheService.createPersistent(name, key, value, maxCount, maxSizeMb);
    }
}
