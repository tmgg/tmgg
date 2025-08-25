package io.tmgg.modules.monitor;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.tmgg.modules.job.JobDesc;
import io.tmgg.modules.job.JobTool;
import io.tmgg.modules.monitor.dao.SysMetricRecordDao;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.slf4j.Logger;

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


        // JVM


    }
}
