package io.tmgg.modules.monitor;

import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.job.JobDesc;
import io.tmgg.modules.job.JobTool;
import io.tmgg.modules.monitor.dao.SysMetricRecordDao;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.slf4j.Logger;
import oshi.hardware.GlobalMemory;

import javax.sql.DataSource;

/**
 * 示例作业
 */
@JobDesc(label = "系统监控")
public class MonitorJob implements Job {

    private static final Logger log = JobTool.getLogger();

    @Resource
    DataSource dataSource;

    @Resource
    SysMetricRecordDao dao;

    @Override
    public void execute(JobExecutionContext e) throws JobExecutionException {
        if(dataSource instanceof HikariDataSource d){
            HikariPoolMXBean mx = d.getHikariPoolMXBean();
            dao.record("datasource.connections.active",mx.getActiveConnections());
            dao.record("datasource.connections.idle",mx.getIdleConnections());
            dao.record("datasource.connections.total",mx.getTotalConnections());
            dao.record("datasource.connections.awaiting",mx.getThreadsAwaitingConnection());
        }

        // CPU 使用率
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();
        System.out.println(cpuInfo);
        dao.record("cpu.used",cpuInfo.getUsed());

        // 内存
        GlobalMemory m = OshiUtil.getMemory();
        long total = m.getTotal();
        long free = m.getAvailable();
        long used = total - free;
        dao.record("mem.used", (used * 1F / total * 100));


        dao.clean();

    }
}
