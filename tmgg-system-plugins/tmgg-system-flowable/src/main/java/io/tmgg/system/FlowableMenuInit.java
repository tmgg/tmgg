package io.tmgg.system;

import io.tmgg.modules.system.service.SysMenuService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FlowableMenuInit implements CommandLineRunner {

    @Resource
    SysMenuService sysMenuService;

    @Override
    public void run(String... args) throws Exception {
        sysMenuService.addMenu("flowableTask","biz", "我的任务","flowableTask","/flowable/task", true);
    }
}
