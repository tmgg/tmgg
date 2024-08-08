package io.tmgg.kettle.dao;

import io.tmgg.kettle.entity.KettleFile;
import io.tmgg.lang.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KettleFileDao extends BaseDao<KettleFile> {
    public KettleFile findByName(String jobName) {
        return this.findOneByField(KettleFile.Fields.name,jobName);
    }

    public List<KettleFile> findByTrans() {
        return this.findAllByField(KettleFile.Fields.fileType, "ktr");
    }
}
