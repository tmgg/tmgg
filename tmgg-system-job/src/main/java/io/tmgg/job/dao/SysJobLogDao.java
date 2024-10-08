package io.tmgg.job.dao;

import io.tmgg.job.entity.SysJob;
import io.tmgg.job.entity.SysJobLog;
import io.tmgg.lang.dao.BaseDao;
import io.tmgg.lang.dao.specification.JpaQuery;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Query;
import java.util.Date;
import java.util.List;

@Repository
public class SysJobLogDao extends BaseDao<SysJobLog> {

    public void deleteByJobId(String jobId) {
        JpaQuery<SysJobLog> q = new JpaQuery<>();
        q.eq(SysJobLog.Fields.sysJob, new SysJob(jobId));
        List<SysJobLog> list = findAll(q);
        this.deleteAll(list);
    }

    @Transactional
    public void clean(Date date) {
        Query q = em.createQuery("delete from SysJobLog where createTime  <= :time" );
        q.setParameter("time", date);
        q.executeUpdate();
    }
}
