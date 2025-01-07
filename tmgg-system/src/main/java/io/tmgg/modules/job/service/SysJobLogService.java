package io.tmgg.modules.job.service;

import io.tmgg.modules.job.entity.SysJobLog;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SysJobLogService extends BaseService<SysJobLog> {

    public SysJobLog findLatest(String jobId) {
        JpaQuery<SysJobLog> q = new JpaQuery<>();
        q.eq(SysJobLog.Fields.sysJob + ".id", jobId);
        return this.findTop1(q, Sort.by(Sort.Direction.DESC, "createTime"));
    }
}
