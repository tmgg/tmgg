package io.tmgg.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StorageStartAppClassNameListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        String cls = event.getSpringApplication().getMainApplicationClass().getName();
        log.info("获取到系统启动类 {}", cls);
        System.setProperty("StartSpringBootApplication", cls);
        System.out.println(System.getProperty("SpringBootApplication"));
    }

}
