package io.tmgg.common.tool;

import io.tmgg.config.FileShiftLogConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class FileShiftLogTool {


    public static Logger getLogger() {
        return LoggerFactory.getLogger(FileShiftLogConfig.LOGGER_NAME);
    }


    public static void setFilename(String value){
        MDC.put(FileShiftLogConfig.DISCRIMINATOR_KEY,value);
    }

    public static void cleanCurrentThread(){
        MDC.clear();
    }
}
