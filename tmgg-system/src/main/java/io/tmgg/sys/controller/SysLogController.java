
package io.tmgg.sys.controller;

import io.tmgg.sys.log.service.SysOpLogService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.log.entity.SysOpLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 系统日志
 */
@RestController
public class SysLogController {


    @Resource
    private SysOpLogService sysOpLogService;





    @HasPermission
    @GetMapping("/sysOpLog/page")
    public AjaxResult opLogPage(@RequestBody SysOpLog sysOpLogParam, @PageableDefault(sort = "opTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return AjaxResult.ok().data(sysOpLogService.findByExampleLike(sysOpLogParam, pageable));
    }








}
