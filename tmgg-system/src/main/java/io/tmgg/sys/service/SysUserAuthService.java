
package io.tmgg.sys.service;

import io.tmgg.core.enums.LogSuccessStatusEnum;
import io.tmgg.core.log.LogManager;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.PasswordTool;
import io.tmgg.sys.app.service.SysConfigService;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.perm.SysPermService;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.role.service.SysRoleService;
import io.tmgg.web.SystemException;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.web.perm.AuthorizingRealm;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import io.tmgg.web.token.TokenManger;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * 认证相关service实现类
 */
@Service
@Slf4j
public class SysUserAuthService implements AuthorizingRealm {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private SysPermService sysPermService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private TokenManger tokenManger;


    public void checkAccount(String account, String password) {
        Assert.hasText(account, "账号不能为空");
        Assert.hasText(password, "密码不能为空");
        SysUser sysUser = sysUserService.getUserByAccount(account);

        Assert.notNull(sysUser, "账号不存在");

        Assert.state(sysUser.getStatus() == CommonStatus.ENABLE, "账号状态异常" + sysUser.getStatus());
        String passwordBcrypt = sysUser.getPassword();

        Assert.hasText(passwordBcrypt, "账号未设置密码");

        boolean checkpw = PasswordTool.checkpw(password, passwordBcrypt);
        Assert.state(checkpw, "密码错误");
    }


    public String createToken(String account) {
        Assert.notNull(account, "账号不能为空");
        SysUser sysUser = sysUserService.getUserByAccount(account);
        Assert.notNull(sysUser, "账号[" + account + "]不存在,请创建或同步账号");


        //生成token
        String token = tokenManger.createToken(sysUser.getId());


        //登录成功，记录登录日志
        LogManager.me().saveLoginLog(sysUser.getAccount(), LogSuccessStatusEnum.SUCCESS.getCode(), null);

        //如果开启限操作用户登陆，则踢掉原来的用户
        Boolean enableSingleLogin = sysConfigService.getEnableSingleLogin();
        if (enableSingleLogin) {
            //获取所有的登陆用户
            List<Subject> allLoginUsers = SecurityUtils.findAll();
            for (Subject subject : allLoginUsers) {
                //如果账号名称相同，并且缓存key和刚刚生成的用户的uuid不一样，则清除以前登录的
                if (subject.getAccount().equals(sysUser.getAccount())) {
                    SecurityUtils.logout(subject.getToken());
                }
            }
        }

        //返回token
        return token;
    }


    /**
     * 获取用户信息
     *
     * @param token
     */
    @Override
    public Subject doGetSubject(String token) {
        //校验token，错误则抛异常
        String userId = tokenManger.validate(token);
        SysUser user = sysUserService.findOne(userId);
        if (user == null) {
            throw new SystemException(401, "用户不存在,请退出重试");
        }

        Subject subject = SecurityUtils.getSubject();
        BeanUtils.copyProperties(user, subject);


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


        // 封存，防止变更权限
        subject.sealed();


        log.info("原始机构权限 {}", subject.getOrgPermissions());
    }


}
