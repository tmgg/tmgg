package io.tmgg.modules.job.service;

import cn.hutool.core.date.DateUtil;
import io.tmgg.modules.job.JobProp;
import io.tmgg.modules.job.dao.SysJobDao;
import io.tmgg.modules.job.dao.SysJobLogDao;
import io.tmgg.modules.job.dao.SysJobLoggingDao;
import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.entity.SysJobLog;
import io.tmgg.modules.job.entity.SysJobLogging;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
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

    @Resource
    private SysJobLogDao sysJobLogDao;

    @Resource
    private SysJobDao sysJobDao;


    public Page<SysJobLogging> read(String jogLogId, Pageable pageable) {
        JpaQuery<SysJobLogging> q = new JpaQuery<>();
        q.eq(SysJobLogging.Fields.jogLogId, jogLogId);

        Page<SysJobLogging> page = sysJobLoggingDao.findAll(q, pageable);

        return page;
    }

    @Resource
    private JobProp jobProp;

    @Async
    @Transactional
    public void cleanByConfig(SysJob job) {
        try {
            int days = jobProp.getMaxHistoryDays();
            sysJobLoggingDao.clean(DateUtil.offsetDay(new Date(), -days));

            int records = jobProp.getMaxHistoryRecords();

            List<SysJobLog> list = sysJobLogDao.findByJobDesc(job);

            if (list.size() > records) {
                List<SysJobLog> targetList = list.subList(records, list.size());
                List<String> ids = targetList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                sysJobLogDao.deleteAll(targetList);
                sysJobLoggingDao.deleteByJobLogId(ids);
            }
        } catch (Exception e) {
            log.error("清理日志失败", e);
        }
    }

}
