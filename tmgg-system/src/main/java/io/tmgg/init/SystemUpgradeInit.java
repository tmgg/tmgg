package io.tmgg.init;

import io.tmgg.dbtool.DbTool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class SystemUpgradeInit {

    @Resource
    private DbTool db;

    public void init() {
        // sys_role_menu 已经不再使用
        db.executeQuietly("drop table sys_role_menu");
    }
}
