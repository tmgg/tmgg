package io.tmgg.job.service;

import io.tmgg.job.entity.SysJob;
import io.tmgg.job.entity.SysJobLog;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SysJobLogService extends BaseService<SysJobLog> {

    public SysJobLog findLatest(){
     return    this.findTop1(null, Sort.by(Sort.Direction.DESC, "createTime"));
    }
}
