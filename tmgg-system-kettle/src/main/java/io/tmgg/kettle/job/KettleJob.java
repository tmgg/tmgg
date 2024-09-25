package io.tmgg.kettle.job;

import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.LogLevel;
import io.tmgg.job.JobLoggerFactory;
import io.tmgg.job.enums.JobDesc;
import io.tmgg.job.enums.JobParamDesc;
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
@JobDesc(name = "Kettle作业",params = {@JobParamDesc(key = "name", label = "作业名称")})
public class KettleJob implements Job {

    private static final Logger log = JobLoggerFactory.getLogger();

    @Resource
    KettleSdk sdk;

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobDataMap dataMap = ctx.getMergedJobDataMap();

        // TODO 这里获取jobid有问题
        String jobId = (String) dataMap.remove("jobId");
        Assert.hasText(jobId,"jobId");


        Map<String, Object> param = dataMap.getWrappedMap();

         String jobName = FilenameUtils.removeExtension(jobId);

        log.info("开始执行kettle任务 {}", jobId);
        sdk.executeJob(jobName, LogLevel.DETAILED, param);


        log.info("结束执行kettle任务 {}", jobId);
    }
}
