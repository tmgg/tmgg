package io.tmgg.kettle.job;

import cn.hutool.core.util.StrUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.tmgg.kettle.sdk.KettleSdk;
import io.github.tmgg.kettle.sdk.response.SlaveServerJobStatus;
import io.github.tmgg.kettle.sdk.response.SlaveServerStatus;
import io.tmgg.modules.job.JobDesc;
import io.tmgg.lang.ann.Msg;
import io.tmgg.modules.sys.msg.IMessagePublishService;
import org.apache.commons.io.IOUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import jakarta.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.List;


@JobDesc(name = "kettle状态检查", params = {})
public class KettleStatusCheckJob implements Job {


    @Resource
    KettleSdk sdk;

    @Resource
    IMessagePublishService messagePublishService;

    private static final Cache<String, String> JOB_CACHE = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofHours(12)).build();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            SlaveServerStatus status = sdk.status();

            List<SlaveServerJobStatus> jobStatusList = status.getJobStatusList();

            for (SlaveServerJobStatus jobStatus : jobStatusList) {
                String statusDescription = jobStatus.getStatusDescription();
                if (StrUtil.contains(statusDescription, "error")) {
                    String jobId = jobStatus.getId();
                    String ifPresent = JOB_CACHE.getIfPresent(jobId);
                    if (ifPresent != null) {
                        // 已经发送过了，不用再次发送
                        continue;
                    }


                    jobStatus = sdk.jobStatus(jobId, jobStatus.getJobName());

                    String title = "作业异常" + statusDescription + ":" + jobStatus.getJobName();
                    StringBuilder sb = new StringBuilder();
                    sb.append("作业ID：").append(jobId).append("\r\n");
                    sb.append("作业名称：").append(jobStatus.getJobName()).append("\r\n");
                    sb.append("日志时间：").append(jobStatus.getLogDate()).append("\r\n");
                    sb.append("状态：").append(jobStatus.getStatusDescription()).append("\r\n");

                    sb.append("\r\n\r\n");
                    sb.append(jobStatus.getLoggingString());


                    messagePublishService.publish("KETTLE_EXCEPTION", title, sb.toString());
                    JOB_CACHE.put(jobId, StrUtil.EMPTY);

                }
            }
        } catch (Exception e) {
            String title = "Kettle状态异常";
            StringWriter sw = new StringWriter();
            PrintWriter out = new PrintWriter(sw);
            e.printStackTrace(out);
            messagePublishService.publish("KETTLE_EXCEPTION", title, out.toString());
            IOUtils.closeQuietly(out, sw);
        }


    }
}
