package io.tmgg.job;

import io.tmgg.lang.ann.Remark;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Remark("任务示例-运行中抛出异常")
public class DemoExceptionJob implements Job {
    @Override
    public void execute(JobExecutionContext e) throws JobExecutionException {
        throw new IllegalStateException("对不起，似乎发生了什么异常");
    }
}
