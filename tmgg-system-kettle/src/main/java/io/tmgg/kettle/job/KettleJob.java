package io.tmgg.kettle.job;

import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.LogLevel;
import io.github.tmgg.kettle.sdk.Result;
import io.tmgg.job.JobLoggerFactory;
import io.tmgg.job.JobRemark;
import io.tmgg.job.Param;
import io.tmgg.kettle.dao.KettleFileDao;
import io.tmgg.kettle.entity.KettleFile;
import io.tmgg.lang.ann.Remark;
import org.quartz.*;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
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

        KettleFile file = kettleFileDao.findByName(jobName);
        Assert.notNull(file, String.format("任务名称[%s]未上传", jobName));

        List<KettleFile> trans = kettleFileDao.findByTrans();
        for (KettleFile tran : trans) {
            sdk.registerTrans(tran.getContent(), null);
        }



        String xml = file.getContent();

        Result result = sdk.registerJob(xml, null);
        Assert.state(result.isSuccess(), result.getMessage());
        log.info("注册任务结果{} {}", result.isSuccess(), result.getMessage());

        String jobId = result.getId();
        result = sdk.startJob(jobId,jobName);
        log.info("执行任务结果:{}", result.getMessage());


        JobDataMap dataMap = ctx.getMergedJobDataMap();
        log.info("开始执行kettle任务 {}", jobName);

        Map<String, Object> param = dataMap.getWrappedMap();

        sdk.executeJob(jobName, LogLevel.DEBUG, param);

        log.info("结束执行kettle任务 {}", jobName);
    }
}
