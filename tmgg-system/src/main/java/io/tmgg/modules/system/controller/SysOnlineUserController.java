
package io.tmgg.modules.system.controller;


import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.system.service.SysOnlineUserService;
import io.tmgg.web.annotion.HasPermission;
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
    @RequestMapping("page")
    public AjaxResult page(Pageable pageable) {
        return AjaxResult.ok().data(sysOnlineUserService.findAll(pageable));
    }

    /**
     * 在线用户强退
     */
    @HasPermission(label = "强制退出")
    @PostMapping("forceExist")
    public AjaxResult forceExist(String sessionId) {
        sysOnlineUserService.forceExist(sessionId);
        return AjaxResult.ok().msg("强退用户成功");
    }
}
