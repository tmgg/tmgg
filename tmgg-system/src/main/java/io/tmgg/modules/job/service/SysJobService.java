package io.tmgg.modules.job.service;

import io.tmgg.modules.job.dao.SysJobLogDao;
import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.JobDescription;
import io.tmgg.modules.job.entity.SysJobLog;
import io.tmgg.modules.job.quartz.QuartzManager;
import io.tmgg.data.service.BaseService;
import io.tmgg.data.query.JpaQuery;
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
    QuartzManager quartzService;

    @Resource
    SysJobLogDao sysJobLogDao;




    public List<SysJob> findAllEnabled() {
        JpaQuery<SysJob> q = new JpaQuery<>();
        q.eq(SysJob.Fields.enabled, true);

        return baseDao.findAll(q);
    }

    @Override
    public SysJob saveOrUpdateByRequest(SysJob input, List<String> updateKeys) throws Exception {
        String jobClass = input.getJobClass();
        SysJob db= super.saveOrUpdateByRequest(input,updateKeys);

        try{
            Class<?> cls = Class.forName(jobClass);
            JobDescription desc = cls.getAnnotation(JobDescription.class);
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

        return null;
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
        }
    }

}
