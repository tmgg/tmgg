package io.tmgg.job.quartz;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;
import io.tmgg.job.dao.SysJobLoggingDao;
import io.tmgg.job.entity.SysJobLogging;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class DBAppender extends AppenderBase<ILoggingEvent> {

    @Resource
    private SysJobLoggingDao sysJobLoggingDao;


    @Override
    protected void append(ILoggingEvent e) {
        saveLogging(e, e.getFormattedMessage());
        if (e.getThrowableProxy() != null) {
            saveLogging(e, e.getThrowableProxy().getMessage());

            for (StackTraceElementProxy s : e.getThrowableProxy().getStackTraceElementProxyArray()) {
                saveLogging(e, s.getSTEAsString());
            }
        }
    }

    private void saveLogging(ILoggingEvent e, String msg) {
        Map<String, String> mdc = e.getMDCPropertyMap();
        SysJobLogging logging = new SysJobLogging();
        logging.setLevel(e.getLevel().levelStr);
        logging.setJobId(mdc.get("job_id"));
        logging.setJogLogId(mdc.get("job_log_id"));
        logging.setMessage(msg);
        logging.setTimeStamp(e.getTimeStamp());
        sysJobLoggingDao.save(logging);
    }


}
