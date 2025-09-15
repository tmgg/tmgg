package io.tmgg.modules.job;

import io.tmgg.modules.job.dao.SysJobDao;
import io.tmgg.lang.SpringTool;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

public class JobTool {



    /**
     * 获得 任务相关参数
     *
     * @param ctx
     * @return
     */
    public static JobDataMap getData(JobExecutionContext ctx) {
        JobDataMap jobDataMap = ctx.getMergedJobDataMap();
        return jobDataMap;
    }

    /**
     * 持久化参数
     *
     * @return
     */
    public static void saveJobData(JobKey jobKey, String dataKey, Object dataValue) {
        SysJobDao sysJobDao = SpringTool.getBean(SysJobDao.class);
        sysJobDao.save(jobKey.getName(), jobKey.getGroup(), dataKey, dataValue);
    }

}
