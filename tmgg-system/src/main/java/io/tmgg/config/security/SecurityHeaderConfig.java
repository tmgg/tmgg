package io.tmgg.config.security;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityHeaderConfig {

    @Bean
    public FilterRegistrationBean<SecurityHeaderFilter> safeFilterRegistrationBean() {
        FilterRegistrationBean<SecurityHeaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SecurityHeaderFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

}
