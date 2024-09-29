package io.tmgg.job.controller;

import io.tmgg.job.entity.SysJob;
import io.tmgg.job.entity.SysJobLog;
import io.tmgg.job.service.SysJobLogService;
import io.tmgg.job.service.SysJobService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("job")
public class SysJobLogController {

    @Resource
    private SysJobLogService service;

    @Resource
    private SysJobService sysJobService;

    @HasPermission
    @PostMapping("jobLog")
    public AjaxResult page(@RequestBody SysJobLog param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        JpaQuery<SysJobLog> q = new JpaQuery<>();

        SysJob sysJob = param.getSysJob();
        if(sysJob != null){
            if(sysJob.getJobClass() != null){
                q.like(SysJobLog.Fields.sysJob + "." + SysJob.Fields.jobClass, sysJob.getJobClass());
            }
            if(sysJob.getName() != null){
                q.like(SysJobLog.Fields.sysJob + "." + SysJob.Fields.name, sysJob.getName());
            }
        }


        if(param.getBeginTime() != null){
            q.gt(SysJobLog.Fields.beginTime, param.getBeginTime());
        }

        if(param.getEndTime() != null){
            q.lt(SysJobLog.Fields.endTime, param.getEndTime());
        }

        Page<SysJobLog> page = service.findAll(q, pageable);
        return AjaxResult.ok().data( page);
    }

    @HasPermission
    @PostMapping("jobLogClean")
    public AjaxResult clean(int cleanDate) {
        sysJobService.clean(cleanDate);
        return AjaxResult.ok();
    }



}
