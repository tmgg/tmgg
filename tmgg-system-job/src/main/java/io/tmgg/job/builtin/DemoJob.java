package io.tmgg.job.builtin;

import io.tmgg.job.JobLoggerFactory;
import io.tmgg.lang.ann.Remark;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

@Remark("示例任务")
public class DemoJob implements Job {
    Logger log = JobLoggerFactory.getLogger();

    @Override
    public void execute(JobExecutionContext e) throws JobExecutionException {
        log.info("示例任务内部日志......{}", "Hello");
    }
}
