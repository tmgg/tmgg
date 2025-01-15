package io.tmgg.modules.job.quartz;

import io.tmgg.modules.job.entity.SysJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class QuartzService {


    @Resource
    private Scheduler scheduler;


    public void deleteJob(SysJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);

        }
    }

    public void scheduleJob(SysJob job) throws SchedulerException, ClassNotFoundException {
        JobDetail jobDetail = getJobDetail(job);

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCron())
                .withMisfireHandlingInstructionFireAndProceed(); // 当调度错失时，执行一次

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(job.getName(), job.getGroup())
                .withSchedule(scheduleBuilder)
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }


    public void triggerJob(SysJob job) throws SchedulerException, ClassNotFoundException {
        if (job.getEnabled()) {
            JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
            scheduler.triggerJob(jobKey);
        } else {
            deleteJob(job);
            log.info("由于job已禁用，创建一个临时job，只执行一次");
            JobDetail jobDetail = getJobDetail(job);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(job.getName(), job.getGroup())
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
                .withIdentity(job.getName(), job.getGroup())
                .usingJobData(new JobDataMap(jobDataMap))
                .build();
        return jobDetail;
    }
}
