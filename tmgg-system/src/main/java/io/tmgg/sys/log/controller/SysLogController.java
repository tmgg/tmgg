
package io.tmgg.sys.log.controller;

import io.tmgg.sys.log.service.SysOpLogService;
import io.tmgg.web.annotion.HasPerm;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.log.entity.SysOpLog;
import io.tmgg.sys.log.entity.SysVisLog;
import io.tmgg.sys.log.service.SysVisLogService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 系统日志
 */
@RestController
public class SysLogController {

    @Resource
    private SysVisLogService sysVisLogService;

    @Resource
    private SysOpLogService sysOpLogService;


    @HasPerm
    @PostMapping("/sysVisLog/page")
    public AjaxResult visLogPage(@RequestBody SysVisLog visLogParam,
                                 @PageableDefault(sort = "visTime", direction = Sort.Direction.DESC)
                                         Pageable pageable) {
        return AjaxResult.ok().data(sysVisLogService.findByExampleLike(visLogParam, pageable));
    }


    @HasPerm
    @PostMapping("/sysOpLog/page")
    public AjaxResult opLogPage(@RequestBody SysOpLog sysOpLogParam, @PageableDefault(sort = "opTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return AjaxResult.ok().data(sysOpLogService.findByExampleLike(sysOpLogParam, pageable));
    }








}
