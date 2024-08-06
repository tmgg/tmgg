package io.tmgg.kettle.dao;

import io.tmgg.kettle.entity.KettleFile;
import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public class KettleFileDao extends BaseDao<KettleFile> {
    public KettleFile findByJobName(String jobName) {
        return this.findOneByField("jobName",jobName);
    }
}
