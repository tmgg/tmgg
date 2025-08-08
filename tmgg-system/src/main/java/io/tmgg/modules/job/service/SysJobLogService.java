package io.tmgg.modules.job.service;

import io.tmgg.modules.job.dao.SysJobLogDao;
import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.entity.SysJobLog;
import io.tmgg.web.persistence.BaseService;
import io.tmgg.web.persistence.specification.JpaQuery;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SysJobLogService extends BaseService<SysJobLog> {

    @Resource
    SysJobLogDao sysJobLogDao;

    @Override
    public String[] getSearchableFields() {
        return new String[]{SysJobLog.Fields.sysJob + "." + SysJob.Fields.name};
    }

    public SysJobLog findLatest(String jobId) {
        JpaQuery<SysJobLog> q = new JpaQuery<>();
        q.eq(SysJobLog.Fields.sysJob + ".id", jobId);
        return this.findTop1(q, Sort.by(Sort.Direction.DESC, "createTime"));
    }

    public SysJobLog findLatest(SysJob job) {
        SysJobLog log = sysJobLogDao.findLatestByJob(job);
        return log;
    }

    public Map<String, Integer> statsTotal(Date begin, Date end) {
        Map<String, Integer> data =new HashMap<>();
        {
            JpaQuery<SysJobLog> q = new JpaQuery<>();
            q.between("createTime", begin, end);
            q.eq(SysJobLog.Fields.success, true);

            int count = (int) sysJobLogDao.count(q);
            data.put("success",count);
        }

        {
            JpaQuery<SysJobLog> q = new JpaQuery<>();
            q.between("createTime", begin, end);
            q.eq(SysJobLog.Fields.success, false);

            int count = (int) sysJobLogDao.count(q);
            data.put("error",count);
        }

        return data;


    }
}
