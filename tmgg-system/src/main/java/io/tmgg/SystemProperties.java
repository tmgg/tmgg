package io.tmgg;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "tmgg.system")
@Data
public class SystemProperties {


    /**
     * 当异常发生时，是否打印异常
     */
    private boolean printException = true;


    /**
     * 站点名称，一般用在前端登录页，顶部logo附件
     */
    private String siteTitle;

    /**
     * 自动更新菜单
     */
    private boolean menuAutoUpdate = true;

    /**
     * 登录验证码
     */
    private boolean captchaEnable = true;

    /**
     * 验证码文件
     */
    private String captchaFiles = "images/captcha/*";

    /**
     * 版权信息
     */
    private String copyright = "Copyright 2024. All rights reserved.";

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
     * 操作日志保留天数
     */
    private int sysOpLogMaxHistoryDays = 30;


    /**
     * 访问日志保留天数
     */
    private int sysVisLogMaxHistoryDays = 30;


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

    /**
     * 模块调试模式，会忽略登录和权限校验
     */
    private boolean enablePermissionAop = true;


}
