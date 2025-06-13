package io.tmgg;

import cn.hutool.core.io.resource.ResourceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 打包时会替换为真实的值，如版本，时间等
 */
public class Build {

    private static String version;

    private static String time;

    public static String getFrameworkVersion() {
        init();
        return version;
    }


    public static String getFrameworkBuildTime() {
        init();
        return time;
    }

    private static void init() {
        if(version != null){
            return;
        }
        try (InputStream is = ResourceUtil.getStream("build-info.properties")) {
            Properties prop = new Properties();
                prop.load(is);
                version = prop.getProperty("version");
                time = prop.getProperty("time");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
