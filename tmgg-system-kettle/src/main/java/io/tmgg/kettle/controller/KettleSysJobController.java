package io.tmgg.kettle.controller;

import cn.hutool.core.bean.BeanUtil;
import io.tmgg.job.entity.SysJob;
import io.tmgg.job.quartz.QuartzManager;
import io.tmgg.job.service.SysJobService;
import io.tmgg.kettle.job.KettleJob;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("kettle/sysJob")
public class KettleSysJobController {

    @Resource
    private SysJobService service;

    @Resource
    private Scheduler scheduler;

    @Resource
    private QuartzManager quartzManager;


    @GetMapping("list")
    public AjaxResult list() {
        JpaQuery<SysJob> q = new JpaQuery<>();
        q.eq("type", "kettle");
        List<SysJob> page = service.findAll(Sort.by(Sort.Direction.DESC, "updateTime"));
        List<Map<String, Object>> list = page.stream().map(job -> {
            Map<String, Object> map = new HashMap<>();
            BeanUtil.copyProperties(job, map);

            if (job.getEnabled()) {
                try {
                    TriggerKey triggerKey = TriggerKey.triggerKey(job.getTriggerKey());


                    Trigger trigger = scheduler.getTrigger(triggerKey);
                    if (trigger != null) {
                        map.put("previousFireTime", trigger.getPreviousFireTime());
                        map.put("nextFireTime", trigger.getNextFireTime());
                    }


                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }


            return map;
        }).collect(Collectors.toList());


        return AjaxResult.ok().data(list);
    }

    @PostMapping("save")
    public AjaxResult save(@RequestBody SysJob param) throws Exception {
        param.setJobClass(KettleJob.class.getName());
        param.setGroup("kettle");

        SysJob result = service.saveOrUpdate(param);
        return AjaxResult.success("操作成功", result.getId());
    }


    @GetMapping("delete")
    public AjaxResult delete(String id) throws SchedulerException {
        service.deleteJob(id);
        return AjaxResult.ok().msg("删除成功");
    }

    @GetMapping("triggerJob")
    public AjaxResult triggerJob(String id) throws SchedulerException, ClassNotFoundException {
        SysJob job = service.findOne(id);
        quartzManager.triggerJob(job);

        return AjaxResult.ok().msg("执行一次命令已发送");
    }


}
