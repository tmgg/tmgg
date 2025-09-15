package io.tmgg.modules.job;

import io.tmgg.modules.log.shift.file.FileShiftLogTool;
import io.tmgg.modules.job.dao.SysJobDao;
import io.tmgg.modules.job.dao.SysJobLogDao;
import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.entity.SysJobLog;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.slf4j.Logger;

import java.util.Date;

@DisallowConcurrentExecution // 不允许并发
public abstract class BaseJob implements Job {

    @Resource
    private SysJobLogDao sysJobLogDao;

    @Resource
    private SysJobDao sysJobDao;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getMergedJobDataMap();
        Logger logger = FileShiftLogTool.getLogger();


        String jobName = context.getJobDetail().getKey().getName();

        // 1. 数据库保存记录
        SysJob job = sysJobDao.findByName(jobName);

        SysJobLog jobLog = new SysJobLog();
        jobLog.setSysJob(job);
        Date fireTime = context.getFireTime();
        jobLog.setBeginTime(fireTime);
        jobLog = sysJobLogDao.save(jobLog);


        // 2. 设置日志
        FileShiftLogTool.start(jobLog.getId());

        String result;
        try {
            result = this.execute(data, logger);
        } catch (Exception e) {
            logger.error("任务执行异常", e);
            result = "异常" + e.getMessage();
            jobLog.setSuccess(false);
        }

        jobLog.setJobRunTime(System.currentTimeMillis() - fireTime.getTime());
        jobLog.setResult(result);
        jobLog.setEndTime(new Date());
        sysJobLogDao.save(jobLog);

        FileShiftLogTool.stop();
    }

    public abstract String execute(JobDataMap data, Logger logger);
}
