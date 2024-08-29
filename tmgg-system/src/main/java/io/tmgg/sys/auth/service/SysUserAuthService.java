
package io.tmgg.sys.auth.service;

import io.tmgg.core.enums.LogSuccessStatusEnum;
import io.tmgg.core.log.LogManager;
import io.tmgg.lang.CodeException;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.IpAddressTool;
import io.tmgg.lang.PasswordTool;
import io.tmgg.sys.auth.AccountCheckResult;
import io.tmgg.sys.consts.service.SysConfigService;
import io.tmgg.sys.menu.service.SysMenuService;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.role.service.SysRoleService;
import io.tmgg.sys.user.entity.SysUser;
import io.tmgg.sys.user.service.SysUserService;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.web.exception.enums.AuthExceptionEnum;
import io.tmgg.web.exception.enums.ServerExceptionEnum;
import io.tmgg.web.perm.AuthorizingRealm;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import io.tmgg.web.token.TokenManger;
import cn.hutool.core.date.DateTime;
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
    private SysMenuService sysMenuService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private TokenManger tokenManger;


    public AccountCheckResult checkAccount(String account, String password) {
        if (ObjectUtil.hasEmpty(account, password)) {
            LogManager.me().saveLoginLog(account, LogSuccessStatusEnum.FAIL.getCode(), AuthExceptionEnum.ACCOUNT_PWD_EMPTY.getMessage());
            return AccountCheckResult.ACC_PWD_EMPTY;
        }

        SysUser sysUser = sysUserService.getUserByAccount(account);


        //用户不存在，账号或密码错误
        if (ObjectUtil.isEmpty(sysUser)) {
            LogManager.me().saveLoginLog(account, LogSuccessStatusEnum.FAIL.getCode(), AuthExceptionEnum.ACCOUNT_PWD_ERROR.getMessage());
            return AccountCheckResult.ACC_NOT_EXIST;
        }
        //验证账号是否被冻结
        CommonStatus status = sysUser.getStatus();
        if (CommonStatus.DISABLE == status ) {
            LogManager.me().saveLoginLog(sysUser.getAccount(), LogSuccessStatusEnum.FAIL.getCode(), AuthExceptionEnum.ACCOUNT_FREEZE_ERROR.getMessage());
            return AccountCheckResult.ACC_DISABLED;
        }

        String passwordBcrypt = sysUser.getPassword();
        //验证账号密码是否正确
        if (ObjectUtil.isEmpty(passwordBcrypt) || !PasswordTool.checkpw(password, passwordBcrypt)) {
            LogManager.me().saveLoginLog(sysUser.getAccount(), LogSuccessStatusEnum.FAIL.getCode(), AuthExceptionEnum.ACCOUNT_PWD_ERROR.getMessage());
            return AccountCheckResult.PWD_ERROR;
        }


        return AccountCheckResult.VALID;
    }


    public String createToken(String account) {
        Assert.notNull(account, "账号不能为空");
        SysUser sysUser = sysUserService.getUserByAccount(account);
        Assert.notNull(sysUser, "账号[" + account + "]不存在,请创建或同步账号");


        //生成token
        String token = tokenManger.createToken(sysUser.getId());

        //设置最后登录ip和时间
        sysUser.setLastLoginIp(IpAddressTool.getIp(HttpServletTool.getRequest()));
        sysUser.setLastLoginTime(DateTime.now());

        //更新用户登录信息
        sysUserService.save(sysUser);

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
     *
     */
    @Override
    public Subject doGetSubject(String token) {
        HttpServletRequest request = HttpServletTool.getRequest();
        if (ObjectUtil.isNull(request)) {
            throw new CodeException(ServerExceptionEnum.REQUEST_EMPTY);
        }

        //校验token，错误则抛异常
        String userId = tokenManger.validate(token);
        SysUser user = sysUserService.findOne(userId);
        if (user == null) {
            throw new CodeException(401, "用户不存在,请退出重试");
        }

        Subject subject = SecurityUtils.getSubject();
        BeanUtils.copyProperties(user, subject);


        subject.setUnitId(user.getOrgId());
        subject.setUnitName(user.getOrgLabel());
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
