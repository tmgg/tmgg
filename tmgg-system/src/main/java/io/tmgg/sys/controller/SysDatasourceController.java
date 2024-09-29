
package io.tmgg.sys.controller;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.service.SysMachineService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("sysDatasource")
public class SysDatasourceController {

    @Resource
    private SysMachineService sysMachineService;

    @Resource
    private DataSource dataSource;


    @HasPermission
    @GetMapping("status")
    public AjaxResult status() {
        Map<String, Object> data = new HashMap<>();
        if (dataSource instanceof HikariDataSource) {
            this.parseHikari((HikariDataSource) dataSource, data);
        }


        return AjaxResult.ok().data(data);
    }


    private void parseHikari(HikariDataSource ds, Map<String, Object> info) {
        HikariPoolMXBean bean = ds.getHikariPoolMXBean();

        info.put("活动连接数",  bean.getActiveConnections());
        info.put("空闲连接数",bean.getIdleConnections());
        info.put("总共连接数",bean.getTotalConnections());
        info.put("等待获取连接线程数",bean.getThreadsAwaitingConnection());

        bean.softEvictConnections();
    }
}
