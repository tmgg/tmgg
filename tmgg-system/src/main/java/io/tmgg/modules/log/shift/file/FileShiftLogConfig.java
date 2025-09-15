package io.tmgg.modules.log.shift.file;


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
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileShiftLogConfig {

    public static final String LOGGER_NAME = "FILE-LOGGER";
    public static final String SIFT_NAME = "FILE-SIFT";
    public static final String DISCRIMINATOR_KEY = "FILE_LOG_ID";
    public static final String DEFAULT_PATH = "/data/logs";

    @Getter
    @Value("${logging.file.path:" + DEFAULT_PATH + "}")
    private String logPath;


    @PostConstruct
    public void init() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // Create and configure the SiftingAppender
        SiftingAppender siftingAppender = new SiftingAppender();
        siftingAppender.setContext(context);
        siftingAppender.setName(SIFT_NAME);

        // Configure the discriminator
        MDCBasedDiscriminator discriminator = new MDCBasedDiscriminator();
        discriminator.setKey(DISCRIMINATOR_KEY);
        discriminator.setDefaultValue("default");
        discriminator.start();

        siftingAppender.setDiscriminator(discriminator);

        // Configure the appender factory
        siftingAppender.setAppenderFactory(new AppenderFactory<ILoggingEvent>() {
            @Override
            public Appender<ILoggingEvent> buildAppender(Context context, String key) throws JoranException {
                FileAppender<ch.qos.logback.classic.spi.ILoggingEvent> appender = new FileAppender<>();
                appender.setContext(context);
                appender.setName("file-" + key);
                appender.setFile(getFile(key));

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
        Logger jobLogger = context.getLogger(LOGGER_NAME);
        jobLogger.setLevel(Level.TRACE);
        jobLogger.setAdditive(false); // additivity="false"
        jobLogger.addAppender(siftingAppender);
    }

    @NotNull
    public String getFile(String key) {
        return logPath + "/" + key + ".log";
    }
}
