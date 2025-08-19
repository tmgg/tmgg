package io.tmgg.modules.system;

import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.modules.system.service.SysConfigService;

/**
 * 表sys_config 的一些便捷函数，常量 （并未包含所有）
 */
public class Configs {

    public static final String RSA_PUBLIC_KEY = "sys.rsa.publicKey";
    public static final String RSA_PRIVATE_KEY = "sys.rsa.privateKey";





    private static SysConfigService service = SpringUtil.getBean(SysConfigService.class);

    public static String get(String key){
        return service.getStr(key);
    }

    public static boolean getBoolean(String key){
        return service.getBoolean(key);
    }

    public static int getInt(String key){
        return service.getInt(key);

    }
}
