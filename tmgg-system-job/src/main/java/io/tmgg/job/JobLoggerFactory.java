package io.tmgg.job;

import io.tmgg.JobProp;
import io.tmgg.lang.SpringTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Deprecated(since = "某个版本移除，请使用JobTool")
public class JobLoggerFactory {


    public static  Logger getLogger(){
        JobProp jobProp = SpringTool.getBean(JobProp.class);

        return LoggerFactory.getLogger(jobProp.getLoggerName());
    }


}
