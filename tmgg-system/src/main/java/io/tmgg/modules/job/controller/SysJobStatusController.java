package io.tmgg.modules.job.controller;

import cn.hutool.core.date.DateUtil;
import io.tmgg.dbtool.DbTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.job.service.SysJobLogService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("jobStatus")
public class SysJobStatusController {


    @Resource
    private Scheduler scheduler;



    @Resource
    private DbTool db;


    @HasPermission
    @RequestMapping("info")
    public AjaxResult info() throws SchedulerException {
        Map<String, Object> rs = new HashMap<>();
        List<JobExecutionContext> list = scheduler.getCurrentlyExecutingJobs();


        String summary = scheduler.getMetaData().getSummary();
        rs.put("summary", summary);


        List<Map<String, Object>> mapList = new ArrayList<>();
        for (JobExecutionContext e : list) {
            Map<String, Object> map = new HashMap<>();
            JobDetail jobDetail = e.getJobDetail();
            map.put("id", e.getFireInstanceId());
            map.put("jobKey", jobDetail.getKey().toString());
            map.put("className", jobDetail.getJobClass().getName());
            map.put("triggerKey", e.getTrigger().getKey().toString());
            map.put("fireTime", DateUtil.formatDateTime(e.getFireTime()));
            map.put("nextFireTime", DateUtil.formatDateTime(e.getNextFireTime()));

            mapList.add(map);
        }
        rs.put("list", mapList);

        return AjaxResult.ok().data(rs);
    }

    @HasPermission(label = "统计报表")
    @RequestMapping("statsTotal")
    public AjaxResult statsTotal() {
        String begin =  DateUtil.offsetDay(new Date(), -30).toDateStr();
        String end = DateUtil.today();
        String sql = """
                SELECT execute_date as date,sum(if(success=1,true,0)) success, sum(if(success=0,true,0)) error from sys_job_log 
                WHERE execute_date BETWEEN ? and ? 
                GROUP BY execute_date
                """;


        return AjaxResult.ok().data(db.findAll(sql,begin, end));

    }


}
