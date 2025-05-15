package io.tmgg;

import io.tmgg.modules.sys.entity.SysMenu;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = SysProp.CONFIG_PREFIX)
@Data
public class SysProp {

    public static final String CONFIG_PREFIX = "sys";




    /**
     * 不经过xss的路径, 如 /aa/*
     */
    private List<String> xssExcludePathList;

    private List<String> loginExcludePathPatterns;

    /**
     * 缓存目录
     */
    private String cacheDir = "/tmgg/cache" ;


    /**
     * 框架菜单
     */
    private List<SysMenu> frameworkMenuList;


}
