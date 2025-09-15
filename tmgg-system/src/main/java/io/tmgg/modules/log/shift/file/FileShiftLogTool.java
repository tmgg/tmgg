package io.tmgg.modules.log.shift.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class FileShiftLogTool {


    public static Logger getLogger() {
        return LoggerFactory.getLogger(FileShiftLogConfig.LOGGER_NAME);
    }


    public static void start(String key) {
        MDC.put(FileShiftLogConfig.DISCRIMINATOR_KEY, key);
    }

    public static void stop() {
        MDC.clear();
    }


}
