package io.tmgg.system;

import io.tmgg.init.SystemHook;
import io.tmgg.modules.system.service.SysMenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import static io.tmgg.common.AntDesignIcon.BranchesOutlined;

@Component
public class FlowableMenuInit implements SystemHook {

    @Resource
    SysMenuService sysMenuService;

    @Override
    public void afterDataInit() {
        sysMenuService.addMenu("biz","flowableTask", "我的任务",null,"flowableTask","/flowable/task", -1,true);
        sysMenuService.addDir("sys","flowableMgr", "流程引擎",BranchesOutlined,10);
        sysMenuService.addMenu("flowableMgr","flowableModel", "模型管理",null,"flowableModel","/flowable", 1,false);
        sysMenuService.addMenu("flowableMgr","flowableMonitor", "流程监控",null,"flowableMonitor","/flowable/monitor", 2,false);
    }


}
