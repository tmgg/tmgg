package io.tmgg.job;

import io.tmgg.JobProp;
import io.tmgg.lang.SpringTool;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JobTool {


    public static  Logger getLogger(){
        JobProp jobProp = SpringTool.getBean(JobProp.class);
        if(jobProp == null){
            return  null;
        }

        return LoggerFactory.getLogger(jobProp.getLoggerName());
    }

    /**
     * 获得 任务相关参数
     * @param ctx
     * @return
     */
    public static JobDataMap getData(JobExecutionContext ctx){
        JobDataMap jobDataMap = ctx.getMergedJobDataMap();
        return jobDataMap;
    }

}
