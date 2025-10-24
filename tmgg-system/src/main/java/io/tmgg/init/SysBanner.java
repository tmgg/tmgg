package io.tmgg.init;

import io.tmgg.config.SysProp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SysBanner implements CommandLineRunner {

    @Resource
    SysProp sysProp;

    @Override
    public void run(String... args) throws Exception {
        log.info("======================================================================");
        log.info("系统数据目录 {}", sysProp.getDataFileDir());
        log.info("如果使用容器部署，建议将该目录持久化。");
        log.info("======================================================================");
    }
}
