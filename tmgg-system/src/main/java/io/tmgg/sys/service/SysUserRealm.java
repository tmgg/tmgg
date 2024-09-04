
package io.tmgg.sys.service;

import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.role.service.SysRoleService;
import io.tmgg.web.SystemException;
import io.tmgg.web.perm.AuthorizingRealm;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;



@Service
@Slf4j
public class SysUserRealm implements AuthorizingRealm {

    @Resource
    private SysUserService sysUserService;



    @Resource
    private SysRoleService sysRoleService;



    /**
     * 获取用户信息
     *
     */
    @Override
    public Subject doGetSubject(String userId) {
        SysUser user = sysUserService.findOne(userId);
        if (user == null) {
            throw new SystemException(401, "用户不存在,请退出重试");
        }

        Subject subject = new Subject();
        BeanUtils.copyProperties(user, subject);

        subject.setId(user.getId());
        subject.setName(user.getName());
        subject.setAccount(user.getAccount());
        subject.setUnitId(user.getUnitId());
        subject.setUnitName(user.getUnitLabel());
        subject.setDeptId(user.getDeptId());
        subject.setDeptName(user.getDeptLabel());

        log.info("获得subject成功 {}", subject);
        return subject;
    }

    /**
     * 获取权限信息
     *
     * @param subject
     */
    @Override
    @Transactional
    public void doGetPermissionInfo(Subject subject) {
        // 角色信息
        Set<SysRole> roles = sysRoleService.getLoginRoles(subject.getId());
        for (SysRole role : roles) {
            subject.addRole(role.getCode());
            List<String> perms = role.getPerms();
            subject.getPermissions().addAll(perms);
        }


        // 数据权限
        Collection<String> loginDataScope = sysUserService.getLoginDataScope(subject.getId());
        subject.getOrgPermissions().addAll(loginDataScope);

        log.info("原始机构权限 {}", subject.getOrgPermissions());
    }


}
