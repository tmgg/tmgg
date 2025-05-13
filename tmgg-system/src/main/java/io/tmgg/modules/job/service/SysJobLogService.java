package io.tmgg.modules.job.service;

import io.tmgg.modules.job.dao.SysJobLogDao;
import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.entity.SysJobLog;
import io.tmgg.persistence.BaseService;
import io.tmgg.persistence.specification.JpaQuery;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SysJobLogService extends BaseService<SysJobLog> {

    @Resource
    SysJobLogDao sysJobLogDao;

    public SysJobLog findLatest(String jobId) {
        JpaQuery<SysJobLog> q = new JpaQuery<>();
        q.eq(SysJobLog.Fields.sysJob + ".id", jobId);
        return this.findTop1(q, Sort.by(Sort.Direction.DESC, "createTime"));
    }

    public SysJobLog findLatest(SysJob job) {
        SysJobLog log = sysJobLogDao.findLatestByJob(job);
        return log;
    }
}
