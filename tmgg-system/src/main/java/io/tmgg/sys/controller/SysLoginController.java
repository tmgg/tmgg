
package io.tmgg.sys.controller;

import cn.hutool.core.util.ObjUtil;
import io.tmgg.SystemProperties;
import io.tmgg.lang.PasswordTool;
import io.tmgg.lang.ann.PublicApi;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.auth.captcha.CaptchaService;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.service.SysUserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 登录控制器
 */
@RestController
@Slf4j
public class SysLoginController {




    @Resource
    private SystemProperties systemProperties;

    @Resource
    private CaptchaService captchaService;

    @Resource
    private SysUserService sysUserService;




    @GetMapping("/check-token")
    @PublicApi
    public AjaxResult loginCheck(HttpSession session) {
        Boolean isLogin = (Boolean) session.getAttribute("isLogin");
        isLogin = ObjUtil.defaultIfNull(isLogin, false);
        return AjaxResult.ok().data(isLogin);
    }

    /**
     * 账号密码登录
     */
    @PostMapping("login")
    public AjaxResult login(@RequestBody LoginParam param, HttpSession session) {
        // 内部系统
        String account = param.getAccount();
        String password = param.getPassword();

        Assert.hasText(password, "请输入密码");
        boolean strengthOk = PasswordTool.isStrengthOk(password);
        Assert.state(strengthOk, "密码强度不够，请联系管理员重置");


        SysUser sysUser = sysUserService.checkLogin(account, password);




        session.setAttribute("isLogin", true);
        session.setAttribute("subjectId", sysUser.getId());

        return AjaxResult.ok().msg("登录成功").data(session.getId());
    }


    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public AjaxResult logout(HttpSession session) {
        session.invalidate();
        return AjaxResult.ok().msg("退出登录成功");
    }


    /**
     * 校验验证码
     */
    private boolean verificationCaptcha(String client) {
        if (systemProperties.isCaptchaEnable()) {
            Assert.hasText(client, "验证码：客户端标识不能为空,请刷新浏览器再试");
            return captchaService.isClientVerifyOK(client);
        }
        return true;
    }

}

