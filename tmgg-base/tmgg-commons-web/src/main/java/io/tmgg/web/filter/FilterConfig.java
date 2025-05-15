package io.tmgg.web.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<CachingRequestBodyFilter> cachingRequestBodyFilterRegistrationBean(CachingRequestBodyFilter filter) {

        FilterRegistrationBean<CachingRequestBodyFilter> registration = new FilterRegistrationBean<>(filter);

        registration.addUrlPatterns("/*");
        return registration;
    }
}
