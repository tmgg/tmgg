package io.tmgg.init;

import io.tmgg.dbtool.DbTool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Slf4j
@Component
public class UpgradeInit {

    @Resource
    private DbTool db;

    public void init() throws SQLException {
        log.info("开始升级程序，主要针对tmgg前框架的升级");
        // sys_role_menu 已经不再使用
        Set<String> tableNames = db.getTableNames();
        if(tableNames.contains("sys_role_menu")){
            db.executeQuietly("drop table sys_role_menu");
        }

    }
}
