package io.tmgg.kettle;

import io.tmgg.job.JobLoggerFactory;
import io.tmgg.job.Param;
import io.tmgg.job.JobRemark;
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
@Remark("Kettle Trans")
@JobRemark(params = {@Param(key = "name", label = "转换名称")})
public class KettleTransJob implements Job {

    private static final Logger log = JobLoggerFactory.getLogger();

    @Resource
    KettleSdk sdk;

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        log.info("开始执行kettle任务");
        JobDataMap dataMap = ctx.getMergedJobDataMap();
        String transName = (String) dataMap.get("name");

        Map<String, Object> param = dataMap.getWrappedMap();


        sdk.executeTrans(transName, LogLevel.DEBUG, param);
    }


}
