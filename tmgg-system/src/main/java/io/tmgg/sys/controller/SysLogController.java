
package io.tmgg.sys.controller;

import io.tmgg.sys.log.service.SysOpLogService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.log.entity.SysLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
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
    public AjaxResult opLogPage(SysLog sysLogParam, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SysLog> page = sysOpLogService.findByExampleLike(sysLogParam, pageable);
        return AjaxResult.ok().data(page).msg("查询操作日志成功");
    }








}
