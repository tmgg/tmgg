
package io.tmgg.modules.sys.controller;

import cn.hutool.core.date.DateUtil;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.service.SysMachineService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@RestController
@RequestMapping("sysDatasource")
public class SysDatasourceController {



    @Resource
    private DataSource dataSource;


    @HasPermission
    @GetMapping("status")
    public AjaxResult status() {
        Map<String, Object> info = new LinkedHashMap<>();

        if (dataSource instanceof HikariDataSource) {
            this.parseHikari((HikariDataSource) dataSource, info);
        }

        return AjaxResult.ok().data(info);
    }


    private void parseHikari(HikariDataSource ds, Map<String, Object> info) {
        HikariPoolMXBean bean = ds.getHikariPoolMXBean();

        info.put("活动连接数",  bean.getActiveConnections());
        info.put("空闲连接数",bean.getIdleConnections());
        info.put("总共连接数",bean.getTotalConnections());
        info.put("等待获取连接线程数",bean.getThreadsAwaitingConnection());

        info.put("配置-jdbcUrl", ds.getJdbcUrl());
        info.put("配置-驱动类",ds.getDriverClassName());
        info.put("配置-连接池类",dataSource.getClass().getName());
        HikariConfigMXBean cfg = ds.getHikariConfigMXBean();
        info.put("配置-最小空闲数 (minimumIdle)",cfg.getMinimumIdle());
        info.put("配置-空闲超时时间秒 (idleTimeout)",  cfg.getIdleTimeout() / 1000);
        info.put("配置-最大连接数 (maximumPoolSize)",cfg.getMaximumPoolSize());
        info.put("配置-连接池名称",cfg.getPoolName());
        bean.softEvictConnections();
    }
}
