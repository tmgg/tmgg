package io.tmgg.kettle.job;

import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.LogLevel;
import io.tmgg.job.JobLoggerFactory;
import io.tmgg.job.JobRemark;
import io.tmgg.job.Param;
import io.tmgg.lang.ann.Remark;
import org.apache.commons.io.FilenameUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 内置 kettle 任务
 *
 * 注意：这里用sysJob的description字段来存储jobName
 */
@Remark("Kettle Job")
@JobRemark(params = {@Param(key = "name", label = "作业名称")})
public class KettleJob implements Job {

    private static final Logger log = JobLoggerFactory.getLogger();

    @Resource
    KettleSdk sdk;

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobDataMap dataMap = ctx.getMergedJobDataMap();

        String description = (String) dataMap.remove("sysJob.description");
        Assert.hasText(description,"描述不能为空");

        String jobId = description;

        Map<String, Object> param = dataMap.getWrappedMap();

         String jobName = FilenameUtils.removeExtension(jobId);

        log.info("开始执行kettle任务 {}", jobId);
        sdk.executeJob(jobName, LogLevel.DETAILED, param);


        log.info("结束执行kettle任务 {}", jobId);
    }
}
