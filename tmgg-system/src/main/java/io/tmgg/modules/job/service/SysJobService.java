package io.tmgg.modules.job.service;

import io.tmgg.modules.job.dao.SysJobTextDao;
import io.tmgg.modules.job.dao.SysJobLogDao;
import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.JobDesc;
import io.tmgg.modules.job.entity.SysJobLog;
import io.tmgg.modules.job.quartz.QuartzService;
import io.tmgg.persistence.BaseService;
import io.tmgg.persistence.specification.JpaQuery;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

import java.util.List;

@Slf4j
@Service
public class SysJobService extends BaseService<SysJob> {

    @Resource
    QuartzService quartzService;

    @Resource
    SysJobLogDao sysJobLogDao;

    @Resource
    SysJobTextDao sysJobTextDao;




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

        quartzService.deleteJob(db);
        if (db.getEnabled()) {
            quartzService.scheduleJob(db);
        }

        return db;
    }


    @Transactional
    public void deleteJob(String id) throws SchedulerException {
        log.info("删除定时任务 {}", id);
        SysJob job = findOne(id);
        Assert.notNull(job, "该任务已被删除，请勿重复操作");
        quartzService.deleteJob(job);

        sysJobLogDao.deleteByJobId(id);

        super.deleteById(id);
    }


    //清理日志
    public void clean(List<String> ids){
        List<SysJobLog> list = sysJobLogDao.findAllById(ids);
        for (SysJobLog sysJobLog : list) {
            sysJobLogDao.delete(sysJobLog);
            sysJobTextDao.delete(sysJobLog.getId());
        }
    }

}
