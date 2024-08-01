package io.tmgg.job.quartz;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.tmgg.job.dao.SysJobDao;
import io.tmgg.job.dao.SysJobLogDao;
import io.tmgg.job.dao.SysJobLoggingDao;
import io.tmgg.job.entity.SysJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Component
@Configuration
@EnableScheduling
public class QuartzConfig implements CommandLineRunner {


    @Resource
    private DBAppender dbAppender;

    @Resource
    private SysJobLoggingDao sysJobLoggingDao;

    @Resource
    private SysJobLogDao sysJobLogDao;


    @Resource
    private Scheduler scheduler;

    @Resource
    private SysJobDao sysJobDao;

    @Resource
    QuartzListener jobExecuteListener;


    @Resource
    QuartzManager quartzManager;


    @Override
    public void run(String... args) throws Exception {
        // 0. 设置logback 配置, 曲波
        this.addLogbackConfig();

        // 1. 添加执行监听器
        scheduler.getListenerManager().addJobListener(jobExecuteListener);

        // 2. 加载数据库任务
        List<SysJob> list = sysJobDao.findAllEnabled();
        for (SysJob sysJob : list) {
            try {
                quartzManager.scheduleJob(sysJob);
            } catch (ClassNotFoundException e) {
                log.error("加载数据库任务失败：{}", e.getMessage());
            }
        }
    }


    private void addLogbackConfig() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        Logger logger = loggerContext.getLogger("job");

        if (logger.getAppender("JOB") == null) {
            logger.setAdditive(false);
            dbAppender.start();
            logger.addAppender(dbAppender);
        }
    }




}
