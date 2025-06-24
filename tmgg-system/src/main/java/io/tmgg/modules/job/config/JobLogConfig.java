package io.tmgg.modules.job.config;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.sift.AppenderFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobLogConfig {

    @Value("${logging.file.path:/data/logs}")
    private String logPath;


    @PostConstruct
    public void init() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        // Create and configure the SiftingAppender
        SiftingAppender siftingAppender = new SiftingAppender();
        siftingAppender.setContext(context);
        siftingAppender.setName("JOB-SIFT");

        // Configure the discriminator
        MDCBasedDiscriminator discriminator = new MDCBasedDiscriminator();
        discriminator.setKey("job_log_id");
        discriminator.setDefaultValue("default");
        discriminator.start();

        siftingAppender.setDiscriminator(discriminator);

        // Configure the appender factory
        siftingAppender.setAppenderFactory(new AppenderFactory<ILoggingEvent>() {
            @Override
            public Appender<ILoggingEvent> buildAppender(Context context, String key) throws JoranException {
                FileAppender<ch.qos.logback.classic.spi.ILoggingEvent> appender = new FileAppender<>();
                appender.setContext(context);
                appender.setName("JOB-" + key);
                appender.setFile(logPath + "/jobs/" + key + ".log");

                PatternLayoutEncoder encoder = new PatternLayoutEncoder();
                encoder.setContext(context);
                encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} - %msg%n");
                encoder.start();

                appender.setEncoder(encoder);
                appender.start();

                return appender;
            }
        });

        siftingAppender.start();

        // Configure the JOB logger
        Logger jobLogger = context.getLogger("JOB");
        jobLogger.setLevel(Level.DEBUG);
        jobLogger.setAdditive(false); // additivity="false"
        jobLogger.addAppender(siftingAppender);
    }
}
