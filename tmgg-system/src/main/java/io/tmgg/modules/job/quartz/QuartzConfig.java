package io.tmgg.modules.job.quartz;

import io.tmgg.modules.job.JobProp;
import io.tmgg.modules.job.dao.SysJobDao;
import io.tmgg.modules.job.entity.SysJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

/***
 * 定时作业
 *
 *
 * @see  io.tmgg.modules.job.builtin.DemoJob
 *
 * @gendoc
 */
@Slf4j
@Component
@Configuration
@EnableScheduling
public class QuartzConfig implements CommandLineRunner {


    @Resource
    private Scheduler scheduler;

    @Resource
    private SysJobDao sysJobDao;

    @Resource
    QuartzListener jobExecuteListener;


    @Resource
    QuartzService quartzService;

    @Resource
    JobProp jobProp;


    @Override
    public void run(String... args) throws Exception {
        if(!jobProp.isEnable()){
            log.warn("定时任务模块已设置全局关闭");
            return;
        }

        // 1. 添加执行监听器
        scheduler.getListenerManager().addJobListener(jobExecuteListener);

        // 2. 加载数据库任务
        List<SysJob> list = sysJobDao.findAllEnabled();
        for (SysJob sysJob : list) {
            try {
                quartzService.scheduleJob(sysJob);
            } catch (ClassNotFoundException e) {
                log.error("加载数据库任务失败：{}", e.getMessage());
            }
        }
    }







}
