package io.tmgg.kettle.job;

import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.LogLevel;
import io.tmgg.job.JobLoggerFactory;
import io.tmgg.job.JobRemark;
import io.tmgg.job.Param;
import io.tmgg.kettle.dao.KettleFileDao;
import io.tmgg.lang.ann.Remark;
import org.apache.commons.io.FilenameUtils;
import org.quartz.*;
import org.slf4j.Logger;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 内置 kettle 任务
 */
@Remark("Kettle Job")
@JobRemark(params = {@Param(key = "name", label = "作业名称")})
public class KettleJob implements Job {

    private static final Logger log = JobLoggerFactory.getLogger();

    @Resource
    KettleSdk sdk;

    @Resource
    KettleFileDao kettleFileDao;

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobDetail jobDetail = ctx.getJobDetail();

        JobKey key = jobDetail.getKey();
        String jobName = key.getName();

        JobDataMap dataMap = ctx.getMergedJobDataMap();
        Map<String, Object> param = dataMap.getWrappedMap();

         jobName = FilenameUtils.removeExtension(jobName);

        log.info("开始执行kettle任务 {}", jobName);
        sdk.executeJob(jobName, LogLevel.BASIC, param);


        log.info("结束执行kettle任务 {}", jobName);
    }
}
