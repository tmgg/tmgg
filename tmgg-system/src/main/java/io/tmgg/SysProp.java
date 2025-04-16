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




}
