package io.tmgg.modules.job.controller;

import cn.hutool.core.date.DateUtil;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("jobStatus")
public class SysJobStatusController {


    @Resource
    private Scheduler scheduler;


    @HasPermission
    @RequestMapping("info")
    public AjaxResult info() throws SchedulerException {
        Map<String,Object> rs = new HashMap<>();
        List<JobExecutionContext> list = scheduler.getCurrentlyExecutingJobs();



        String summary = scheduler.getMetaData().getSummary();
        rs.put("summary",summary);


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
        rs.put("list",mapList);



        return AjaxResult.ok().data(rs);
    }

}
