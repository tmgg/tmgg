
package io.tmgg.sys.monitor.controller;


import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.monitor.result.SysOnlineUserResult;
import io.tmgg.sys.monitor.service.SysOnlineUserService;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("sysOnlineUser")
public class SysOnlineUserController {

    @Resource
    private SysOnlineUserService sysOnlineUserService;

    /**
     * 在线用户列表
     *
     */
    @HasPermission
    @PostMapping("page")
    public AjaxResult page(Pageable pageable) {
        Page<SysOnlineUserResult> page = sysOnlineUserService.findAll(pageable);
        return AjaxResult.ok().data(page);
    }

    /**
     * 在线用户强退
     */
    @HasPermission
    @PostMapping("forceExist")
    public AjaxResult forceExist(String sessionId) {
        sysOnlineUserService.forceExist(sessionId);
        return AjaxResult.ok().msg("强退用户成功");
    }
}
