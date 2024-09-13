package io.tmgg.sys.controller.app;


import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.service.SysUserService;
import io.tmgg.web.perm.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("app/user")
public class AppUserController {

    @Resource
    SysUserService sysUserService;

    /**
     * 当前用户
     * @return
     */
    @GetMapping("info")
    public AjaxResult info(){
        String id = SecurityUtils.getSubject().getId();
        SysUser user = sysUserService.findOne(id);

        return AjaxResult.ok().data(user);
    }

}
