package io.tmgg.job.dao;

import io.tmgg.job.entity.SysJob;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SysJobDao extends BaseDao<SysJob> {

    public List<SysJob> findAllEnabled() {
        JpaQuery<SysJob> q = new JpaQuery<>();
        q.eq(SysJob.Fields.enabled, true);


        return this.findAll(q);
    }

    public SysJob findByName(String name) {
        JpaQuery<SysJob> q = new JpaQuery<>();
        q.eq(SysJob.Fields.name, name);


        return this.findOne(q);
    }

}
