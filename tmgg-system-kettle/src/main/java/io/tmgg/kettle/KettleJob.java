package io.tmgg.kettle;

import io.tmgg.job.JobLoggerFactory;
import io.tmgg.job.JobRemark;
import io.tmgg.job.Param;
import io.tmgg.lang.ann.Remark;
import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.LogLevel;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 内置 kettle 任务
 */
@Remark("Kettle Job")
@JobRemark(params = {@Param(key = "name", label = "作结名称")})
public class KettleJob implements Job {

    private static final Logger log = JobLoggerFactory.getLogger();

    @Resource
    KettleSdk sdk;

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {

        JobDataMap dataMap = ctx.getMergedJobDataMap();
        String jobName = (String) dataMap.get("name");
        log.info("开始执行kettle任务 {}", jobName);

        Map<String, Object> param = dataMap.getWrappedMap();

        sdk.executeJob(jobName, LogLevel.DEBUG, param);
    }
}
