package io.tmgg;

import cn.hutool.core.io.resource.ResourceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 打包时会替换为真实的值，如版本，时间等
 */
public class Build {


    public static final String FRAMEWORK_VERSION;
    public static final String FRAMEWORK_BUILD_TIME;

    static {
        InputStream is = ResourceUtil.getStream("build.properties");
        Properties prop = new Properties();
        try {
            prop.load(is);
            FRAMEWORK_VERSION = prop.getProperty("version");
            FRAMEWORK_BUILD_TIME = prop.getProperty("time");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
