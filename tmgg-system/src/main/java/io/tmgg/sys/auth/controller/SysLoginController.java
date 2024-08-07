
package io.tmgg.sys.auth.controller;

import io.tmgg.SystemProperties;
import io.tmgg.core.event.LogoutEvent;
import io.tmgg.core.log.LogManager;
import io.tmgg.lang.InnerTokenTool;
import io.tmgg.lang.PasswordTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.sys.auth.AccountCheckResult;
import io.tmgg.sys.auth.captcha.CaptchaService;
import io.tmgg.sys.auth.service.SysUserAuthService;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import io.tmgg.web.token.TokenManger;
import cn.hutool.core.text.PasswdStrength;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 登录控制器
 */
@RestController
@Slf4j
public class SysLoginController {

    @Resource
    private SysUserAuthService authService;


    @Resource
    private SystemProperties systemProperties;

    @Resource
    private CaptchaService captchaService;

    @Resource
    private TokenManger tokenManger;

    /**
     * 账号密码登录
     */
    @PostMapping("login")
    public AjaxResult login(@RequestBody LoginParam param) {
        // 内部系统
        String account = param.getAccount();
        String password = param.getPassword();

        Assert.hasText(password,"请输入密码");
        boolean strengthOk = PasswordTool.isStrengthOk(password);

        if(!strengthOk) {
            return AjaxResult.err().msg("密码强度不够，请联系管理员重置");
        }

       if(param.getToken() != null){
           account = InnerTokenTool.validateToken(param.getToken());
       }else {
           //检测是否开启验证码
           boolean codeValid = verificationCaptcha(param.getClientId());
           if (!codeValid) {
               return AjaxResult.error(401, "验证码错误");
           }

           AccountCheckResult resultEnum = authService.checkAccount(account, password);
           Assert.state(resultEnum == AccountCheckResult.VALID, resultEnum.getMessage());
       }

        String token = authService.createToken(account);

        return AjaxResult.success("登录成功", token);
    }





    /**
     * 退出登录
     *
     * @param request
     */
    @GetMapping("/logout")
    public AjaxResult logout(HttpServletRequest request) {
        String token = tokenManger.getTokenFromRequest(request);
        Subject subject = SecurityUtils.getCachedSubjectByToken(token);
        if (subject != null && subject.isAuthenticated() && subject.getId() != null) {
            log.info("用户退出 {} {}", subject.getName(), subject.getAccount());
            LogManager.me().saveLogoutLog(subject.getAccount() + subject.getName());
            SecurityUtils.logout(subject.getToken());
            SpringUtil.getApplicationContext().publishEvent(new LogoutEvent(this, subject.getId()));
        }

        request.getSession().invalidate();

        return AjaxResult.success("退出登录成功");
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

@Data
class LoginParam {
    String account;
    String password;
    String code;

    // 外部系统登录的token
    String token;

    String clientId; // 客户端标识，主要用于验证码二次验证
}
