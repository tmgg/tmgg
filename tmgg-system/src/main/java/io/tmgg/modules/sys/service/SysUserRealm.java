
package io.tmgg.modules.sys.service;

import io.tmgg.framework.session.SysHttpSession;
import io.tmgg.modules.sys.entity.SysUser;
import io.tmgg.modules.sys.entity.SysRole;
import io.tmgg.web.BizException;
import io.tmgg.web.perm.AuthorizingRealm;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
     */
    @Override
    public Subject doGetSubject(HttpSession session, String userId) {
        Subject subject = (Subject) session.getAttribute(SysHttpSession.SUBJECT_KEY);
        if (subject == null) {
            subject = new Subject();
            SysUser user = sysUserService.findOne(userId);
            log.debug("查询用户  {}", user);
            if (user == null) {
                throw new BizException("用户或密码错误"); // 不直接提示用户不存在，防止暴力遍历
            }

            BeanUtils.copyProperties(user, subject);

            subject.setId(user.getId());
            subject.setName(user.getName());
            subject.setAccount(user.getAccount());
            subject.setUnitId(user.getUnitId());
            subject.setUnitName(user.getUnitLabel());
            subject.setDeptId(user.getDeptId());
            subject.setDeptName(user.getDeptLabel());


            fillPermissions(subject);

        }


        return subject;
    }

    private void fillPermissions(Subject subject) {
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
    }


}
