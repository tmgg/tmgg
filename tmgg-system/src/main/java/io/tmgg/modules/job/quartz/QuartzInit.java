package io.tmgg.modules.job.quartz;

import io.tmgg.config.SysProp;
import io.tmgg.lang.SpringTool;
import io.tmgg.modules.job.dao.SysJobDao;
import io.tmgg.modules.job.entity.SysJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

/***
 * 作业调度
 *
 */
@Slf4j
@Component
@Configuration
@EnableScheduling
public class QuartzInit implements CommandLineRunner {



    @Resource
    private SysJobDao sysJobDao;




    @Resource
    QuartzManager quartzService;

    @Resource
    SysProp sysProp;


    @Override
    public void run(String... args) throws Exception {
        if(!sysProp.isJobEnable()){
            log.warn("定时任务模块已设置全局关闭");
            return;
        }
        // 兼容性代码
        if("false".equals(SpringTool.getProperty("sys.job.enable"))){
            log.warn("【兼容性提示】请将sys.job.enable配置替换为sys.jobEnable");
            log.warn("定时任务模块已设置全局关闭");
            return;
        }


        // 2. 加载数据库任务
        List<SysJob> list = sysJobDao.findAllEnabled();
        for (SysJob sysJob : list) {
            try {
                log.info("加载定时任务: {} {}", sysJob.getName(), sysJob.getJobClass());
                quartzService.scheduleJob(sysJob);
            } catch (ClassNotFoundException e) {
                log.error("加载数据库任务失败：{}", e.getMessage());
            }
        }
    }


}
