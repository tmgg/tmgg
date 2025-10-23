package io.tmgg.modules.system.controller;

import io.tmgg.framework.session.SysHttpSessionService;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.system.dto.request.UpdatePwdRequest;
import io.tmgg.modules.system.entity.SysRole;
import io.tmgg.modules.system.entity.SysUser;
import io.tmgg.modules.system.service.SysUserService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("userCenter")
public class UserCenterController {

    @Resource
    private  SysUserService sysUserService;

    @Resource
    private SysHttpSessionService sm;

    @RequestMapping("info")
    public AjaxResult info(){
        Subject subject = SecurityUtils.getSubject();
        String name = subject.getName();

        SysUser user = sysUserService.findOne(subject.getId());

        Set<String> roles = user.getRoles().stream().map(SysRole::getName).collect(Collectors.toSet());

        return AjaxResult.ok().data("name",name)
                .data("phone",user.getPhone())
                .data("dept",subject.getDeptName())
                .data("unit",subject.getUnitName())
                .data("roles", roles)
                .data("email", user.getEmail())
                .data("account", user.getAccount())
                .data("createTime", user.getCreateTime())
                ;
    }




    @PostMapping("updatePwd")
    public AjaxResult updatePwd(@RequestBody UpdatePwdRequest request) {
        String userId = SecurityUtils.getSubject().getId();
        String newPassword = request.getNewPassword();
        sysUserService.updatePwd(userId,  newPassword);
        sm.forceExistBySubjectId(userId);
        return AjaxResult.ok();
    }
}
