package io.tmgg.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JobLoggerFactory {


    public static  Logger getLogger(){
        return LoggerFactory.getLogger("job");
    }


}
