package io.tmgg.kettle.job;

import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.LogLevel;
import io.tmgg.modules.job.BaseJob;
import io.tmgg.modules.job.JobDesc;
import io.tmgg.modules.job.JobTool;
import jakarta.annotation.Resource;
import org.apache.commons.io.FilenameUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 内置 kettle 任务
 * <p>
 * 注意：这里用sysJob的description字段来存储jobName
 */
@JobDesc(label = "Kettle作业", group = "kettle", paramsProvider = KettleJobParamFieldProvider.class)
public class KettleJob extends BaseJob {

    public static final String JOB_PARAM_FILE = "file";

    @Resource
    KettleSdk sdk;




    @Override
    public String execute(JobDataMap data, Logger logger) throws Exception {
        // TODO 这里获取jobid有问题
        String file = (String) data.get(JOB_PARAM_FILE);
        Assert.hasText(file, "作业不能为空");


        Map<String, Object> param = data.getWrappedMap();

        String jobName = FilenameUtils.removeExtension(file);

        logger.info("开始执行kettle任务 {}", file);
        sdk.executeJob(jobName, LogLevel.DETAILED, param);


        logger.info("结束执行kettle任务 {}", file);

        return "ok";
    }


}
