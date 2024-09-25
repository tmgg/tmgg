package io.tmgg.job.quartz;

import io.tmgg.job.entity.SysJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Map;

@Slf4j
@Component
public class QuartzManager {


    @Resource
    private Scheduler scheduler;


    public void deleteJob(SysJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getName(),job.getGroup());
        if (scheduler.checkExists(jobKey)) {
            scheduler.pauseTrigger(TriggerKey.triggerKey(job.getTriggerKey()));
            scheduler.deleteJob(jobKey);
        }
    }

    public void scheduleJob(SysJob job) throws SchedulerException, ClassNotFoundException {
        JobDetail jobDetail = getJobDetail(job);

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(job.getTriggerKey(), job.getGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(job.getCron()))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);

    }


    public void triggerJob(SysJob job) throws SchedulerException, ClassNotFoundException {
        if (job.getEnabled()) {
            JobKey jobKey = JobKey.jobKey(job.getName(),job.getGroup());
            scheduler.triggerJob(jobKey);
        } else {
            deleteJob(job);
            log.info("由于job已禁用，创建一个临时job，只执行一次");
            JobDetail jobDetail = getJobDetail(job);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(job.getTriggerKey(), job.getGroup())
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withRepeatCount(0)) // 不重复
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    private static JobDetail getJobDetail(SysJob job) throws ClassNotFoundException {
        String jobClass = job.getJobClass();
        Class<? extends Job> cls = (Class<? extends Job>) Class.forName(jobClass);

        Map<String, Object> jobDataMap = job.getJobDataMap();


        JobDetail jobDetail = JobBuilder.newJob(cls)
                .withIdentity(job.getName(),job.getGroup())
                .usingJobData(new JobDataMap(jobDataMap))
                .build();
        return jobDetail;
    }
}
