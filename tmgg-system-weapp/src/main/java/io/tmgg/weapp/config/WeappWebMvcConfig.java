
package io.tmgg.weapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * web配置
 */
@Slf4j
@Configuration
public class WeappWebMvcConfig implements WebMvcConfigurer {


    @Resource
    WeappInteceptor weappInteceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
         registry.addInterceptor(weappInteceptor)
                .addPathPatterns("/app/weapp/**");

    }



}
