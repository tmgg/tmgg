package io.tmgg.job.builtin;

import io.tmgg.lang.ann.Remark;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Remark("Demo-异常")
public class DemoExceptionJob implements Job {
    @Override
    public void execute(JobExecutionContext e) throws JobExecutionException {
        throw new IllegalStateException("对不起，似乎发生了什么异常");
    }
}
