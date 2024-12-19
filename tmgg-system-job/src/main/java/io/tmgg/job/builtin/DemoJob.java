package io.tmgg.job.builtin;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import io.tmgg.job.JobLoggerFactory;
import io.tmgg.job.JobDesc;
import io.tmgg.data.FieldDesc;
import io.tmgg.sys.msg.EmailService;
import jakarta.annotation.Resource;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.util.Assert;

/**
 * 示例任务
 */
@JobDesc(name = "示例任务-发送系统状态", params = {@FieldDesc(name = "email", label = "接收邮箱", required = true)})
public class DemoJob implements Job {

    private static final Logger log = JobLoggerFactory.getLogger();

    @Resource
    EmailService emailService;

    @Override
    public void execute(JobExecutionContext e) throws JobExecutionException {
        log.info("开始执行任务-邮件发送系统状态");

        // 获取参数
        String email = e.getJobDetail().getJobDataMap().getString("email");
        Assert.hasText(email,"请填写邮箱"); // 抛出异常

        log.info("接收邮箱为：{}",email);

        StringBuilder sb = new StringBuilder();
        sb.append("空闲内存:").append(FileUtil.readableFileSize( SystemUtil.getFreeMemory())).append(" ");
        sb.append("总内存：").append(FileUtil.readableFileSize(SystemUtil.getTotalMemory())).append(" ");
        String content = sb.toString();
        log.info("内容为 {}", content);

        emailService.send(email, "系统状态", content);

    }
}
