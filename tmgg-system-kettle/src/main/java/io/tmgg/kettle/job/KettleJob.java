package io.tmgg.kettle.job;

import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.LogLevel;
import io.tmgg.data.Field;
import io.tmgg.job.JobLoggerFactory;
import io.tmgg.job.JobDesc;
import io.tmgg.data.FieldAnn;
import io.tmgg.job.JobParamFieldProvider;
import io.tmgg.job.entity.SysJob;
import org.apache.commons.io.FilenameUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 内置 kettle 任务
 *
 * 注意：这里用sysJob的description字段来存储jobName
 */
@JobDesc(name = "Kettle作业", params = {@FieldAnn(name = "name", label = "作业名称")})
public class KettleJob implements Job, JobParamFieldProvider {

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

    @Override
    public List<Field> getFields(JobDesc jobDesc) {
        List<Field> list = new ArrayList<>();
        Field field = new Field();
        list.add(field);


        return list;
    }
}
