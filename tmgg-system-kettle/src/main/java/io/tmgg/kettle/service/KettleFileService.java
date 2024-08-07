package io.tmgg.kettle.service;

import io.tmgg.kettle.entity.KettleFile;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KettleFileService extends BaseService<KettleFile> {
    public List<KettleFile> findJobList() {
        JpaQuery<KettleFile> q = new JpaQuery<>();
        q.eq(KettleFile.Fields.fileType, "kjb");
        List<KettleFile> list = baseDao.findAll(q,Sort.by(Sort.Direction.DESC, "updateTime"));

        return list;
    }
}
