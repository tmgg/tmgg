package io.tmgg.modules.job.dao;

import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.entity.SysJobLog;
import io.tmgg.web.persistence.BaseDao;
import io.tmgg.web.persistence.specification.JpaQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SysJobLogDao extends BaseDao<SysJobLog> {

    public void deleteByJobId(String jobId) {
        JpaQuery<SysJobLog> q = new JpaQuery<>();
        q.eq(SysJobLog.Fields.sysJob, new SysJob(jobId));
        List<SysJobLog> list = findAll(q);
        this.deleteAll(list);
    }






    /**
     * 查询执行记录，倒叙
     * @param job
     * @return
     */
    public List<SysJobLog> findByJobDesc(SysJob job) {
        JpaQuery<SysJobLog> q = new JpaQuery<>();
        q.eq(SysJobLog.Fields.sysJob, job);
        return this.findAll(q, Sort.by(Sort.Direction.DESC,"createTime"));
    }

    public SysJobLog findLatestByJob(SysJob job) {
        JpaQuery<SysJobLog> q = new JpaQuery<>();
        q.eq(SysJobLog.Fields.sysJob, job);
        return this.findTop1(q, Sort.by(Sort.Direction.DESC,"createTime"));
    }

    public long countByJob(SysJob job) {
        JpaQuery<SysJobLog> q = new JpaQuery<>();
        q.eq(SysJobLog.Fields.sysJob, job);
        return this.count(q);
    }

}
