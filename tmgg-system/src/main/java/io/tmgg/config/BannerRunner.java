package io.tmgg.config;

import cn.hutool.core.date.DateUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Date;

@Component
public class BannerRunner implements CommandLineRunner {


    @Override
    public void run(String... args) throws Exception {
        URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
        String file = location.getFile();

        final String line = "---------------------------------------------------------------------------------------------------------------";

        System.out.println();
        System.out.println(line);
        System.out.println("\t框架代码文件版本为：" + file);
        File f = new File(file);
        if (f.exists()) {
            System.out.println("\t本地更新时间：" + DateUtil.formatDateTime(new Date(f.lastModified())));
        }
        System.out.println(line);
        System.out.println();
    }
}
