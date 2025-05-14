package io.tmgg.modules.job.dao;

import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.web.persistence.BaseDao;
import io.tmgg.web.persistence.specification.JpaQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public SysJob findByNameAndGroup(String name, String group) {
        JpaQuery<SysJob> q = new JpaQuery<>();
        q.eq(SysJob.Fields.name, name);
        q.eq(SysJob.Fields.group, group);

        return this.findOne(q);
    }

    @Transactional
    public void save(String name, String group, String paramKey, Object paramValue) {
        SysJob job = this.findByNameAndGroup(name, group);
        Assert.notNull(job, "没有找到数据库中的job " + name);

        Map<String, Object> jobData = job.getJobData();
        if(jobData == null){
            jobData = new HashMap<>();
            job.setJobData(jobData);
        }
        jobData.put(paramKey, paramValue);
        this.save(job);
    }
}
