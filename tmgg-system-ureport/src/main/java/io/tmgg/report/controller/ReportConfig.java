package io.tmgg.report.controller;

import io.tmgg.core.filter.xss.XssFilter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;

@Configuration
public class ReportConfig implements ApplicationRunner {

    @Resource
    XssFilter xssFilter;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        xssFilter.excludePath("/ureport/**");
    }


}
