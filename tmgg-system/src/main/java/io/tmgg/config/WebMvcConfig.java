
package io.tmgg.config;

import io.tmgg.core.filter.xss.XssFilter;
import io.tmgg.framework.interceptor.SecurityInterceptor;
import io.tmgg.framework.interceptor.SubjectInterceptor;
import io.tmgg.SysProp;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Resource;

/**
 * web配置
 */
@Slf4j
@Configuration
@EnableScheduling
@ConditionalOnClass(name = "org.springframework.web.servlet.config.annotation.WebMvcConfigurer")
public class WebMvcConfig implements WebMvcConfigurer {


    @Resource
    private SysProp sysProp;


    @Bean
 public    XssFilter xssFilter() {
        return new XssFilter();
    }


    /**
     * 放开权限校验的接口
     */
    private static final String[] NONE_SECURITY_URL_PATTERNS = {
            "/wx/**",
            "/app/weapp/**",
            "/**/**.jpg",
            "/web/**",

            "/favicon.ico",

            //后端的
            "/",
            "/login",
            "/logout",
            "/oauth/**",
            "/error",
    };









    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器
        HandlerInterceptor[] interceptors = new HandlerInterceptor[]{new SecurityInterceptor(),
                new SubjectInterceptor()};
        for (HandlerInterceptor interceptor : interceptors) {
            InterceptorRegistration registration = registry.addInterceptor(interceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns("/**login**/**")
                    .excludePathPatterns("/public/**")

                    // 静态资源, 乐观模式，认为包含符号 “.”的就是静态资源， 如果后端请求写了url 包含 . ,则会存在漏洞
                    .excludePathPatterns("/**/*.**")

                    .excludePathPatterns("/")
                    .excludePathPatterns("/*.**");


            if (CollUtil.isNotEmpty(sysProp.getLoginExcludePathPatterns())) {
                registration.excludePathPatterns(sysProp.getLoginExcludePathPatterns());
            }

            registration.excludePathPatterns(NONE_SECURITY_URL_PATTERNS);
        }


    }

    /**
     * xss过滤器
     *
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterFilterRegistrationBean(XssFilter xssFilter) {
        // 忽略流程引擎
        xssFilter.excludePath("/flowable/**");

        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>(xssFilter);

        registration.addUrlPatterns("/*");
        return registration;
    }




}
