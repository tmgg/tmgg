package io.tmgg.framework.session.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

/***
 * 会话
 *
 * 框架使用session， 并缓存在内存或硬盘
 *
 * 代码中可直接使用HttpSession存储一些登录用户的数据
 *
 * 为什么使用session认证
 * 为了集成一些第三方功能页面，如 ureport。 使用session后，不用再考虑集成认证。
 *
 * @gendoc
 */
@Configuration
@EnableSpringHttpSession
public class MySessionConfig {

    @Bean
    public MySessionRepository sessionRepository() {
        return new MySessionRepository();
    }


    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return new CookieHttpSessionIdResolver();
    }



}
