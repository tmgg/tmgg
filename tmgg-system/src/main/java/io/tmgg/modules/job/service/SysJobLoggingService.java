package io.tmgg.modules.job.service;

import cn.hutool.core.date.DateUtil;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.modules.job.JobProp;
import io.tmgg.modules.job.dao.SysJobDao;
import io.tmgg.modules.job.dao.SysJobLogDao;
import io.tmgg.modules.job.dao.SysJobLoggingDao;
import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.entity.SysJobLog;
import io.tmgg.modules.job.entity.SysJobLogging;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysJobLoggingService {

    @Resource
    private SysJobLoggingDao sysJobLoggingDao;




    public Page<SysJobLogging> read(String jogLogId, Pageable pageable) {
        JpaQuery<SysJobLogging> q = new JpaQuery<>();
        q.eq(SysJobLogging.Fields.jogLogId, jogLogId);

        Page<SysJobLogging> page = sysJobLoggingDao.findAll(q, pageable);

        return page;
    }





}
