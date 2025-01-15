package io.tmgg.modules.job.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("jobStatus")
public class SysJobStatusController {


    @Resource
    private Scheduler scheduler;


    @HasPermission
    @RequestMapping("list")
    public AjaxResult list() throws SchedulerException {
        List<JobExecutionContext> list = scheduler.getCurrentlyExecutingJobs();

        List<Map<String, Object>> mapList = list.stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            JobDetail jobDetail = e.getJobDetail();
            map.put("id", IdUtil.fastSimpleUUID());

            map.put("name", jobDetail.getKey().getName());
            map.put("className", jobDetail.getJobClass().getName());
            map.put("fireTime", DateUtil.formatDateTime(e.getFireTime()));

            map.put("jobRunTime", e.getJobRunTime());


            return map;
        }).toList();


        return AjaxResult.ok().data(mapList);
    }

}
