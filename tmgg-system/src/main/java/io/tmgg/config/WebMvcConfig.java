
package io.tmgg.config;

import cn.hutool.core.collection.CollUtil;
import io.tmgg.SysProp;
import io.tmgg.framework.xss.XssFilter;
import io.tmgg.framework.interceptor.AppApiJwtInterceptor;
import io.tmgg.framework.interceptor.LoginInterceptor;
import io.tmgg.framework.interceptor.PermissionInterceptor;
import io.tmgg.framework.interceptor.SubjectInterceptor;
import io.tmgg.web.WebConstants;
import io.tmgg.web.argument.resolver.RequestBodyKeysArgumentResolver;
import io.tmgg.web.argument.resolver.UnpagedPageableArgumentResolver;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

/**
 * web配置
 */
@Slf4j
@Configuration
@EnableScheduling
@EnableCaching
@ConditionalOnClass(name = "org.springframework.web.servlet.config.annotation.WebMvcConfigurer")
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 放开权限校验的接口
     */
    private static final String[] NONE_SECURITY_URL_PATTERNS = {
            // 前端静态文件
            "/**.jpg",
            "/**.png",
            "/static/**.jpg",
            "/static/**.png",
            "/favicon.ico",
            "/web/**",


            //后端的
            "/",
            "/login",
            "/logout",
            "/oauth/**",
            "/error",

            // 接口
            "/openApi/gateway/**",

            // 移动端, 小程序
            WebConstants.APP_API_PATTERN
    };


    @Resource
    private SysProp sysProp;

    @Resource
    private PermissionInterceptor permissionInterceptor;

    @Resource
    private AppApiJwtInterceptor appApiInterceptor;

    @Bean
    public XssFilter xssFilter() {
        return new XssFilter();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器
        HandlerInterceptor[] interceptors = new HandlerInterceptor[]{
                new LoginInterceptor(),
                new SubjectInterceptor(),
                permissionInterceptor
        };
        for (HandlerInterceptor interceptor : interceptors) {
            InterceptorRegistration registration = registry.addInterceptor(interceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns("/**login**/**")
                    .excludePathPatterns("/public/**")


                    // 静态资源, 乐观模式，认为包含符号 “.”的就是静态资源， 如果后端请求写了url 包含 . ,则会存在漏洞
                    .excludePathPatterns("/**/*.*")
                    .excludePathPatterns("/")
                    .excludePathPatterns("/*.*");


            List<String> loginExcludePathPatterns = sysProp.getLoginExcludePathPatterns();
            if (CollUtil.isNotEmpty(loginExcludePathPatterns)) {
                log.info("忽略鉴权的地址: {}", loginExcludePathPatterns);
                registration.excludePathPatterns(loginExcludePathPatterns);
            }

            registration.excludePathPatterns(NONE_SECURITY_URL_PATTERNS);
        }


        registry.addInterceptor(appApiInterceptor).addPathPatterns(WebConstants.APP_API_PATTERN);

    }

    /**
     * xss过滤器
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterFilterRegistrationBean(XssFilter xssFilter) {
        // 忽略流程引擎
        xssFilter.excludePath("/flowable/**");

        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>(xssFilter);

        registration.addUrlPatterns("/*");
        return registration;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String[] list = {
                "classpath:/META-INF/resources/",
                "classpath:/resources/",
                "classpath:/static/",
                "classpath:/public/",

                // 同级目录下的静态文件
                "file:./static/"
        };

        registry.addResourceHandler("/**").addResourceLocations(list);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(0,new UnpagedPageableArgumentResolver());
        resolvers.add(new RequestBodyKeysArgumentResolver());
    }

    /**
     * 由于引入了 jackson-dataformat-xml， 导致浏览器打开接口时返回xml(浏览器请求头Accept含xml)，这里设置默认返回json
     * @param configurer
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.ignoreAcceptHeader(true)
                .defaultContentType(MediaType.APPLICATION_JSON);
    }
}
