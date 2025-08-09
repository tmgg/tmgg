package io.tmgg.modules.api;

import cn.hutool.crypto.SecureUtil;
import org.springframework.util.Assert;

public class ApiSignTool {
    public static String sign( String appId, String appSecret,  long timestamp) {
        String signStr = appId + appSecret +timestamp ;
        return SecureUtil.md5(signStr);
    }

}
