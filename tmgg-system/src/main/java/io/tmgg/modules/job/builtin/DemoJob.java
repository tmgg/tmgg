package io.tmgg.modules.job.builtin;

import io.tmgg.lang.field.FieldInfo;
import io.tmgg.modules.job.JobDesc;
import io.tmgg.modules.job.JobTool;
import org.quartz.*;
import org.slf4j.Logger;

/**
 * 示例作业
 */
@DisallowConcurrentExecution // 不允许并发则加这个注解
@JobDesc(name = "示例作业-发送系统状态", params = {@FieldInfo(name = "msg", label = "打印信息", required = true)})
public class DemoJob implements Job {

    private static final Logger log = JobTool.getLogger();


    @Override
    public void execute(JobExecutionContext e) throws JobExecutionException {
        log.info("开始执行任务-邮件发送系统状态");

        // 获取参数
        JobDataMap data = JobTool.getData(e);
        String msg = data.getString("msg");

        System.out.println(msg);
    }
}
