
package io.tmgg.weixin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Resource;

/**
 * web配置
 */
@Slf4j
@Configuration
public class WeixinWebMvcConfig implements WebMvcConfigurer {


    @Resource
 private    WeixinInteceptor weixinInteceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
         registry.addInterceptor(weixinInteceptor)
                .addPathPatterns("/app/weixin/**");

    }



}
