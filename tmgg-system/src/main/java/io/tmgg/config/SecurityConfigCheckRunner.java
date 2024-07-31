package io.tmgg.config;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Order(Integer.MIN_VALUE)
public class SecurityConfigCheckRunner implements CommandLineRunner {


    @Override
    public void run(String... args) throws Exception {
        Map<String, String> map = new HashMap<>();

        map.put("server.servlet.session.cookie.same-site", "strict");
        map.put("server.servlet.session.cookie.secure", "true");

        for (Map.Entry<String, String> e : map.entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            String sysValue = SpringUtil.getProperty(k);
            if (sysValue == null || !sysValue.equals(v)) {
                log.error("----------------------------------------------------");
                log.error("请配置 {} 的值为 {}", k, v);
                log.error("----------------------------------------------------");
                System.exit(-1);
            }
        }


    }
}
