
package io.tmgg.modules;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ICaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjUtil;
import io.tmgg.lang.PasswordTool;
import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.auth.LoginAttemptService;
import io.tmgg.modules.sys.controller.LoginParam;
import io.tmgg.modules.sys.entity.SysUser;
import io.tmgg.modules.sys.service.SysConfigService;
import io.tmgg.modules.sys.service.SysUserService;
import io.tmgg.web.CodeException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountLockedException;
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

    @Resource
    private LoginAttemptService loginAttemptService;


    @GetMapping("checkLogin")
    @PublicRequest
    public AjaxResult checkLogin(HttpSession session) {
        Boolean isLogin = (Boolean) session.getAttribute("isLogin");
        Boolean needUpdatePwd = (Boolean) session.getAttribute("needUpdatePwd");

        isLogin = ObjUtil.defaultIfNull(isLogin, false);
        needUpdatePwd = ObjUtil.defaultIfNull(needUpdatePwd, false);

        return AjaxResult.ok().data("isLogin",isLogin).data("needUpdatePwd",needUpdatePwd);
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

        boolean locked = loginAttemptService.isAccountLocked(account);
        Assert.state(!locked,"账户已被锁定，请30分钟后再试");

        ThreadUtil.sleep(1000); // 防止黑客爆破

        // 验证码校验
        if (sysConfigService.getBoolean("sys.siteInfo.captcha")) {
            Assert.hasText(param.getCode(), "请输入验证码");

            String sessionCode = (String) session.getAttribute(CAPTCHA_CODE);

            Assert.state(getCodeGenerator().verify(sessionCode, param.getCode()), "验证码错误");

            session.removeAttribute(CAPTCHA_CODE);
        }

        try {
            SysUser sysUser = sysUserService.checkLogin(account, password);
            loginAttemptService.loginSucceeded(account); // 登录成功清除记录

            // 检查是否需要修改密码
            boolean needUpdatePwd = sysUserService.checkNeedUpdatePwd(account, password);

            session.setAttribute("subjectId", sysUser.getId());
            session.setAttribute("needUpdatePwd", needUpdatePwd);
            session.setAttribute("isLogin", true);
            return AjaxResult.ok().msg("登录成功").data(session.getId());
        }catch (Exception e){
            if(e instanceof CodeException ce){
                if(ce.getCode() == SysUserService.CODE_PWD_ERR){
                    loginAttemptService.loginFailed(account);
                }
            }

            throw e;
        }
    }


    /**
     * 退出登录
     */
    @GetMapping("logout")
    public AjaxResult logout(HttpSession session) {
        session.invalidate();
        return AjaxResult.ok();
    }


    @PublicRequest
    @GetMapping("captchaImage")
    public void captcha(HttpSession session, HttpServletResponse resp) throws IOException {
        try {


            CodeGenerator generator = getCodeGenerator();

            ICaptcha captcha = CaptchaUtil.createLineCaptcha(100, 50, generator, 100);


            captcha.write(resp.getOutputStream());

            String code = captcha.getCode();
            session.setAttribute(CAPTCHA_CODE, code);
        } catch (Exception e) {
            log.error("生成验证码失败，将验证码参数设置为禁用");
            sysConfigService.setBoolean("sys.siteInfo.captcha",false);

        }
    }

    private CodeGenerator getCodeGenerator() {
        String captchaType = sysConfigService.getStr("sys.siteInfo.captchaType");
        CodeGenerator generator;
        if ("math".equals(captchaType)) {
            generator = new MathGenerator(2);
        } else {
            generator = new RandomGenerator(4);
        }
        return generator;
    }


}

