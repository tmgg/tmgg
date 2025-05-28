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
@JobDesc(label = "示例作业", params = {@FieldInfo(name = "msg", label = "打印信息")})
public class DemoJob implements Job {

    private static final Logger log = JobTool.getLogger();


    @Override
    public void execute(JobExecutionContext e) throws JobExecutionException {
        log.info("开始执行任务");

        // 获取参数
        JobDataMap data = JobTool.getData(e);
        String msg = data.getString("msg");

        System.out.println(msg);
        log.info("打印信息：{}", msg);

        e.setResult("结果：成功");
    }
}
