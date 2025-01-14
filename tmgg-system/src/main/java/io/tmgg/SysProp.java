package io.tmgg;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = SysProp.CONFIG_PREFIX)
@Data
public class SysProp {

    public static final String CONFIG_PREFIX = "sys";
    private Class  basePackageClass = BasePackage.class;


    /**
     * 适用Assert时
     * 是否打印异常堆栈
     */
    private boolean printExceptionForAssert = false;




    /**
     * 自动更新菜单,枚举，数据库（database目录下的json文件)
     */
    private boolean autoUpdateSysData = true;

    /**
     * 强制更新database目录下的json文件到数据库
     */
    private boolean foreUpdateJsonDatabase = false;


    /**
     * 页面右上角，点击关于显示的内容文件
     */
    private String aboutFile = "about.md";


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
     * 机构预留字段1
     */
    private boolean sysOrgReservedField1Enable = false;
    private String sysOrgReservedField1Label;
    private ValueType sysOrgReservedField1ValueType = ValueType.TEXT;

    /**
     * 机构预留字段2
     */
    private boolean sysOrgReservedField2Enable = false;
    private String sysOrgReservedField2Label;
    private ValueType sysOrgReservedField2ValueType = ValueType.TEXT;

    /**
     * 机构预留字段3
     */
    private boolean sysOrgReservedField3Enable = false;
    private String sysOrgReservedField3Label;
    private ValueType sysOrgReservedField3ValueType = ValueType.TEXT;


    public enum ValueType {
        TEXT,
        BOOLEAN,
    }

    private SiteInfo siteInfo = new SiteInfo();


    @Data
    public static class SiteInfo {
        String title;
        String copyright;

        /**
         * 这个地址主要用来拼接图片等资源的全路径, 如https://baidu.com
         * 注意：非必需，除非遇到多层转发
         * 实践中发现经过多层waf或ningx等的转发，如果管理员不熟悉，则会引起获取请求路径失败的情况
          */
        String url;
    }



}
