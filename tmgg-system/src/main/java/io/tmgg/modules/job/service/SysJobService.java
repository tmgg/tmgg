package io.tmgg.modules.job.service;

import io.tmgg.modules.job.dao.SysJobLogDao;
import io.tmgg.modules.job.dao.SysJobLoggingDao;
import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.JobDesc;
import io.tmgg.modules.job.quartz.QuartzManager;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.lang.dao.specification.JpaQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SysJobService extends BaseService<SysJob> {

    @Resource
    QuartzManager quartzManager;

    @Resource
    SysJobLogDao sysJobLogDao;

    @Resource
    SysJobLoggingDao sysJobLoggingDao;

    public List<SysJob> findAllEnabled() {
        JpaQuery<SysJob> q = new JpaQuery<>();
        q.eq(SysJob.Fields.enabled, true);


        return baseDao.findAll(q);
    }

    @Override
    public SysJob saveOrUpdate(SysJob input) throws Exception {
        String jobClass = input.getJobClass();
        SysJob db = super.saveOrUpdate(input);

        try{
            Class<?> cls = Class.forName(jobClass);
            JobDesc desc = cls.getAnnotation(JobDesc.class);
            if(desc!= null ){
                db.setGroup(desc.group());
            }
        }catch (Exception e){
            throw new IllegalStateException(jobClass + "不存在，请确认");
        }

        quartzManager.deleteJob(db);
        if (db.getEnabled()) {
            quartzManager.scheduleJob(db);
        }

        return db;
    }


    @Transactional
    public void deleteJob(String id) throws SchedulerException {
        log.info("删除定时任务 {}", id);
        SysJob job = findOne(id);
        Assert.notNull(job, "该任务已被删除，请勿重复操作");
        quartzManager.deleteJob(job);

        sysJobLoggingDao.deleteByJobId(id);
        sysJobLogDao.deleteByJobId(id);


        super.deleteById(id);
    }


    //清理日志
    public void clean(Integer cleanDate){
        // 删除10天以前的
        Date date = new Date();
        date = DateUtils.addDays(date, -cleanDate);

        sysJobLoggingDao.clean(date);
        sysJobLogDao.clean(date);
    }

}
