package io.tmgg.job.controller;

import io.tmgg.job.entity.SysJobLog;
import io.tmgg.job.service.SysJobLogService;
import io.tmgg.job.service.SysJobService;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPerm;
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

    @HasPerm
    @PostMapping("jobLog")
    public AjaxResult page(@RequestBody SysJobLog param, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) {
        Page<SysJobLog> page = service.findByExampleLike(param, pageable);
        return AjaxResult.ok().data( page);
    }

    @HasPerm
    @PostMapping("jobLogClean")
    public AjaxResult clean(int cleanDate) {
        sysJobService.clean(cleanDate);
        return AjaxResult.ok();
    }



}
