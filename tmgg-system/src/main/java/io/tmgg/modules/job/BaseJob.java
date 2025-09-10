package io.tmgg.modules.job;

import org.quartz.*;
import org.slf4j.Logger;

import java.util.Map;

@DisallowConcurrentExecution // 不允许并发
public abstract class BaseJob implements Job {


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = JobTool.getData(jobExecutionContext);
        Logger logger = JobTool.getLogger();

        String result = this.execute(data, logger);

        jobExecutionContext.setResult(result);
    }

    public abstract String execute(JobDataMap data, Logger logger);
}
