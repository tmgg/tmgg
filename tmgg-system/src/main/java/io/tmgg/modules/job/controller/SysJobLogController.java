package io.tmgg.modules.job.controller;

import io.tmgg.modules.job.entity.SysJob;
import io.tmgg.modules.job.entity.SysJobLog;
import io.tmgg.modules.job.service.SysJobLogService;
import io.tmgg.modules.job.service.SysJobService;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("job")
public class SysJobLogController {

    @Resource
    private SysJobLogService service;

    @Resource
    private SysJobService sysJobService;

    @HasPermission
    @RequestMapping("jobLog")
    public AjaxResult page(SysJobLog param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
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
