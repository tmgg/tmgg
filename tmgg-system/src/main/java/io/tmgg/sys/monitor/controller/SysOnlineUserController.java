
package io.tmgg.sys.monitor.controller;


import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.monitor.service.SysOnlineUserService;
import io.tmgg.web.annotion.BusinessLog;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class SysOnlineUserController {

    @Resource
    private SysOnlineUserService sysOnlineUserService;

    /**
     * 在线用户列表
     *
     */
    @HasPermission
    @PostMapping("/sysOnlineUser/page")
    @BusinessLog("系统在线用户_列表")
    public AjaxResult page(Pageable pageable) {
        return AjaxResult.success(sysOnlineUserService.findAll(pageable));
    }

    /**
     * 在线用户强退
     */
    @HasPermission
    @PostMapping("/sysOnlineUser/forceExist")
    @BusinessLog("系统在线用户_强退")
    public AjaxResult forceExist(String sessionId) {
        sysOnlineUserService.forceExist(sessionId);
        return AjaxResult.success();
    }
}
