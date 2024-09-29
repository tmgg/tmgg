
package io.tmgg.sys.controller;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjUtil;
import io.tmgg.lang.PasswordTool;
import io.tmgg.lang.ann.PublicApi;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.service.SysConfigService;
import io.tmgg.sys.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 登录控制器
 */
@RestController
@Slf4j
public class SysLoginController {


    public static final String CAPTCHA_CODE = "captchaCode";




    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysConfigService sysConfigService;



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


        ThreadUtil.sleep(1000); // 防止黑客爆破

        // 验证码校验
        if(sysConfigService.getBoolean("siteInfo.captcha")){
            Assert.hasText(param.getCode(),"请输入验证码");

            String code = (String) session.getAttribute(CAPTCHA_CODE);
            Assert.state(code.equalsIgnoreCase(param.getCode()), "验证码错误");
        }

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


    @PublicApi
    @GetMapping("captchaImage")
    public void captcha(HttpSession session, HttpServletResponse resp) throws IOException {
        AbstractCaptcha captcha = CaptchaUtil.createLineCaptcha(100, 50,4 ,100);

        captcha.write(resp.getOutputStream());

        String code = captcha.getCode();
        session.setAttribute(CAPTCHA_CODE, code);

    }


}

