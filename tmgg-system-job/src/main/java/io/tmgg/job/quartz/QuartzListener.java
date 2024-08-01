package io.tmgg.job.quartz;

import io.tmgg.job.JobLoggerFactory;
import io.tmgg.job.dao.SysJobDao;
import io.tmgg.job.dao.SysJobLogDao;
import io.tmgg.job.entity.SysJob;
import io.tmgg.job.entity.SysJobLog;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class QuartzListener implements JobListener {


    private static final Logger log = JobLoggerFactory.getLogger();

    @Resource
    private SysJobLogDao sysJobLogDao;

    @Resource
    private SysJobDao sysJobDao;


    @Override
    public String getName() {
        return "JobExecuteListener";
    }


    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().getName();

        // 1. 数据库保存记录

        SysJob job = sysJobDao.findByName(jobName);

        SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setSysJob(job);
        sysJobLog.setBeginTime(context.getFireTime());
        sysJobLog = sysJobLogDao.save(sysJobLog);


        // 2. 设置日志
        MDC.put("job_id", job.getId());
        MDC.put("job_log_id", sysJobLog.getId());

    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {

    }


    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        String jobName = context.getJobDetail().getKey().getName();
        String result = (String) context.getResult();

        if (jobException != null) {
            result = jobException.getMessage();
            log.error("任务执行异常", jobException);
        }



        Date now = new Date();

        SysJob job = sysJobDao.findByName(jobName);
        sysJobDao.save(job);

        String jobLogId = MDC.get("job_log_id");
        SysJobLog jobLog = sysJobLogDao.findOne(jobLogId);
        jobLog.setJobRunTime(context.getJobRunTime());
        jobLog.setEndTime(now);

        jobLog.setResult(result);
        sysJobLogDao.save(jobLog);

        MDC.clear();

    }


}
