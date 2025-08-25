package io.tmgg.modules.monitor.dao;

import io.tmgg.modules.monitor.entity.SysMetricRecord;
import io.tmgg.web.persistence.BaseDao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SysMetricRecordDao extends BaseDao<SysMetricRecord> {

    @Transactional
    public void record( String name, Number value) {
        SysMetricRecord r = new SysMetricRecord();
        r.setMetricName(name);
        r.setMetricValue(value.doubleValue());
        super.save(r);
    }


}
