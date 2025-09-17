package io.tmgg.modules.monitor;

import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.tmgg.modules.job.BaseJob;
import io.tmgg.modules.job.JobDesc;
import io.tmgg.modules.monitor.dao.SysMetricRecordDao;
import jakarta.annotation.Resource;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import oshi.hardware.GlobalMemory;

import javax.sql.DataSource;

/**
 * 示例作业
 */
@JobDesc(group = "系统监控", label = "指标数据记录(CPU，内存，数据库等)")
public class MonitorJob extends BaseJob {


    @Resource
    DataSource dataSource;

    @Resource
    SysMetricRecordDao dao;


    @Override
    public String execute(JobDataMap data, Logger logger) throws Exception{
        logger.info("开始采集");
        if (dataSource instanceof HikariDataSource d) {
            HikariPoolMXBean mx = d.getHikariPoolMXBean();
            dao.record("datasource.connections.active", mx.getActiveConnections());
        }

        // CPU 使用率
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();
        dao.record("cpu.usage", cpuInfo.getUsed());

        // 内存
        GlobalMemory m = OshiUtil.getMemory();
        long total = m.getTotal();
        long free = m.getAvailable();
        long used = total - free;
        dao.record("mem.usage", (used * 1F / total * 100));


        dao.clean();
        logger.info("开始完成");
        return "采集成功";
    }
}
