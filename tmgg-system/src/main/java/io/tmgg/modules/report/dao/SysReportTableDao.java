package io.tmgg.modules.report.dao;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.report.entity.SysReportChart;
import io.tmgg.modules.report.entity.SysReportTable;
import org.springframework.stereotype.Repository;

@Repository
public class SysReportTableDao extends BaseDao<SysReportTable> {

    public SysReportTable findByCode(String code) {
        JpaQuery<SysReportTable> q= new JpaQuery<>();
        q.eq(SysReportTable.Fields.code, code);
        return this.findOne(q);
    }
}

