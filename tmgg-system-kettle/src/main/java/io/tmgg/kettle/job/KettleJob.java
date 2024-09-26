package io.tmgg.kettle.job;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.LogLevel;
import io.tmgg.data.Field;
import io.tmgg.job.JobDesc;
import io.tmgg.job.JobLoggerFactory;
import io.tmgg.job.JobParamFieldProvider;
import io.tmgg.kettle.KettleFileService;
import io.tmgg.kettle.controller.JobXmlInfo;
import jakarta.annotation.Resource;
import org.apache.commons.io.FilenameUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内置 kettle 任务
 * <p>
 * 注意：这里用sysJob的description字段来存储jobName
 */
@JobDesc(name = "Kettle作业", group = "kettle", paramsProvider = KettleJobParamFieldProvider.class)
public class KettleJob implements Job {

    private static final Logger log = JobLoggerFactory.getLogger();
    public static final String JOB_PARAM_FILE = "file";

    @Resource
    KettleSdk sdk;



    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobDataMap dataMap = ctx.getMergedJobDataMap();

        // TODO 这里获取jobid有问题
        String file = (String) dataMap.get(JOB_PARAM_FILE);
        Assert.hasText(file, "作业不能为空");


        Map<String, Object> param = dataMap.getWrappedMap();

        String jobName = FilenameUtils.removeExtension(file);

        log.info("开始执行kettle任务 {}", file);
        sdk.executeJob(jobName, LogLevel.DETAILED, param);


        log.info("结束执行kettle任务 {}", file);
    }


}
