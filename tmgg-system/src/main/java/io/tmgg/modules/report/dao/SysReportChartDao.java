package io.tmgg.modules.report.dao;

import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.report.entity.SysReportChart;
import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class SysReportChartDao extends BaseDao<SysReportChart> {

    public SysReportChart findByCode(String code) {
        JpaQuery<SysReportChart> q = new JpaQuery<>();
        return this.findOne(q);
    }
}

