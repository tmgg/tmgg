package io.tmgg.job.service;

import io.tmgg.job.dao.SysJobLoggingDao;
import io.tmgg.job.entity.SysJobLogging;
import io.tmgg.lang.dao.specification.JpaQuery;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SysJobLoggingService {

    @Resource
    SysJobLoggingDao dao;


    public Page<SysJobLogging> read(String jogLogId) {
        JpaQuery<SysJobLogging> q = new JpaQuery<>();
        q.eq(SysJobLogging.Fields.jogLogId, jogLogId);

        Sort sort = Sort.by(Sort.Direction.ASC, SysJobLogging.Fields.timeStamp);
        PageRequest pageable = PageRequest.of(0, 10000, sort);

        Page<SysJobLogging> page = dao.findAll(q, pageable);


        return page;
    }


}
