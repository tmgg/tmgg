package io.tmgg.modules.monitor.dao;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.tmgg.dbtool.DbTool;
import io.tmgg.modules.monitor.entity.SysMetricRecord;
import io.tmgg.web.persistence.BaseDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class SysMetricRecordDao extends BaseDao<SysMetricRecord> {

    @Resource
    DbTool db;

    @Transactional
    public void record( String name, Number value) {
        SysMetricRecord r = new SysMetricRecord();
        r.setMetricName(name);
        r.setMetricValue(value.doubleValue());
        super.save(r);

    }

    /**
     * 清除一个月前的数据
     */
    public void clean() {
        String sql = "delete from sys_metric_record where create_time < ?";
        DateTime lastMonth = DateUtil.lastMonth();
        int rows = db.execute(sql, lastMonth);
        log.info("清理一个月前数据 {}条", rows);
    }

}
