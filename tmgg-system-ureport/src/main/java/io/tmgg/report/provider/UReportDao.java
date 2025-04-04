package io.tmgg.report.provider;

import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;



@Slf4j
@Repository
public class UReportDao extends BaseDao<UReport> {


    public UReport findByFile(String file) {
        JpaQuery<UReport> q = new JpaQuery<>();
        q.eq(UReport.Fields.file, file);
        return this.findOne(q);
    }

}
