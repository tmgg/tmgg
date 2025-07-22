
package io.tmgg.modules.system.controller;

import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;


@RestController
@RequestMapping("sysDatasource")
public class SysDatasourceController {


    @Resource
    private DataSource dataSource;

    @HasPermission
    @GetMapping("config")
    public AjaxResult config() {
        Map<String, Object> info = new LinkedHashMap<>();

        if (dataSource instanceof HikariDataSource ds) {
            info.put("jdbcUrl", ds.getJdbcUrl());
            info.put("driverClassName", ds.getDriverClassName());
            info.put("连接池", dataSource.getClass().getName());

            HikariConfigMXBean cfg = ds.getHikariConfigMXBean();
            info.put("minimumIdle", cfg.getMinimumIdle());
            info.put("idleTimeout", cfg.getIdleTimeout() / 1000);
            info.put("maximumPoolSize", cfg.getMaximumPoolSize());
            info.put("poolName", cfg.getPoolName());
        }

        return AjaxResult.ok().data(info);
    }

    @HasPermission
    @GetMapping("status")
    public AjaxResult status() {
        Map<String, Object> info = new LinkedHashMap<>();

        if (dataSource instanceof HikariDataSource ds) {
            HikariPoolMXBean bean = ds.getHikariPoolMXBean();

            info.put("activeConnections", bean.getActiveConnections());
            info.put("idleConnections", bean.getIdleConnections());
            info.put("totalConnections", bean.getTotalConnections());
            info.put("threadsAwaitingConnection", bean.getThreadsAwaitingConnection());
        }

        return AjaxResult.ok().data(info);
    }


}
