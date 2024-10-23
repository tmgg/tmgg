package io.tmgg.framework.session.config;

import io.tmgg.framework.cache.EhCacheTool;
import io.tmgg.framework.session.SysHttpSession;
import org.ehcache.Cache;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

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
    public Cache<String, SysHttpSession> httpSessionCache(){
        return EhCacheTool.getInstance().createCache("httpSession",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class,
                                SysHttpSession.class,
                                ResourcePoolsBuilder.heap(1000).disk(10, MemoryUnit.MB, true)
                        )
                        .build());

    }
}
