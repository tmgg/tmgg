package io.tmgg.report.provider;

import io.tmgg.lang.dao.BaseDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;



@Slf4j
@Repository
public class UReportDao extends BaseDao<UReport> {


    public UReport findByFile(String file) {
        return this.findOneByField(UReport.Fields.file, file);
    }

}
