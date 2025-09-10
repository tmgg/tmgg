package io.tmgg.config;

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
    private String cacheDir = "/data/cache";


    /**
     * 允许上传文件的后缀， 如 docx
     */
    private String allowUploadFiles = "docx,xlsx,pdf,png,jpg,mp3,mp4,wav";


    /**
     * session空闲时间（分钟），超过该时间则登录失效
     */
    private int sessionIdleTime = 180;

    /***
     * 登录锁定时间（分钟）
     */
    private int loginLockTime = 5;


    /**
     * 登录异常最大次数， 超过则锁定
     */
    private int loginLockMaxAttempts = 10;
}
