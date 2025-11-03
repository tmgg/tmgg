package io.tmgg.modules.system.controller;

import io.tmgg.framework.session.SysHttpSessionService;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.system.dto.request.UpdatePwdRequest;
import io.tmgg.modules.system.dto.response.UserResponse;
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

        UserResponse user = sysUserService.findOneDto(subject.getId());

        return AjaxResult.ok().data("name",name)
                .data("phone",user.getPhone())
                .data("dept",user.getDeptLabel())
                .data("unit",user.getUnitLabel())
                .data("roles", user.getRoleNames())
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
