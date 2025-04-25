package com.xxx.app;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaRunStepInfo;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.tmgg.framework.interceptor.AppApiJwtInterceptor;
import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.entity.SysUser;
import io.tmgg.modules.sys.service.JwtService;
import io.tmgg.modules.sys.service.SysUserMessageService;
import io.tmgg.modules.sys.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(AppApiJwtInterceptor.APP_API)
public class AppController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private JwtService jwtService;

    @Resource
    private SysUserMessageService sysUserMessageService;

    @Resource
    private WxMaService wxMaService;


    /**
     * 测试网络
     *
     * @return
     */
    @PublicRequest
    @GetMapping("ping")
    public AjaxResult ping() {
        String userId = jwtService.getSubject();
        AjaxResult r = AjaxResult.ok().msg("pong");
        if (userId != null) {
            SysUser user = sysUserService.findOne(userId);
            r.data(user);
        }

        return r;
    }

    @PublicRequest
    @PostMapping("loginByWeixin")
    public AjaxResult loginByWeixin(String code) throws WxErrorException {
        WxMaJscode2SessionResult r = wxMaService.getUserService().getSessionInfo(code);
        String openid = r.getOpenid();
        String sessionKey = r.getSessionKey();

        try {
            DateTime expireTime = DateUtil.offsetDay(DateUtil.date(), 2); // sessionKey最多3天
            String token = jwtService.createToken(openid, expireTime,Map.of("sessionKey",sessionKey));

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("expireTime", expireTime.getTime());

            return AjaxResult.ok().data(data).msg("通过openid登录成功");
        } catch (Exception e) {
            log.info("登录失败", e);

            return AjaxResult.err(e.getMessage());
        }
    }


    @PublicRequest
    @RequestMapping("login")
    public AjaxResult login(String username, String password) {
        try {
            SysUser sysUser = sysUserService.checkLogin(username, password);

            DateTime expireTime = DateUtil.nextMonth();
            String token = jwtService.createToken(sysUser.getId(), expireTime);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("expireTime", expireTime.getTime());

            return AjaxResult.ok().data(data);
        } catch (Exception e) {
            log.info("登录失败", e);

            return AjaxResult.err(e.getMessage());
        }
    }


    /**
     * 查看未读消息个数
     *
     * @return
     */
    @RequestMapping("messageCount")
    public AjaxResult messageCount() {
        String userId = jwtService.getSubject();
        long count = sysUserMessageService.countUnReadByUser(userId);

        return AjaxResult.ok().data(count);
    }

    @PublicRequest
    @RequestMapping("logout")
    public AjaxResult logout(HttpServletRequest request) {
        String token = jwtService.getToken(request);
        jwtService.disableToken(token);
        return AjaxResult.ok().msg("退出登录成功");
    }

    @RequestMapping("getWeRunData")
    public AjaxResult getWeRunData(String encryptedData, String iv) {
        String openId = jwtService.getSubject();
        String sessionKey = (String) jwtService.getJwt().getData().get("sessionKey");

        List<WxMaRunStepInfo> runStepInfo = wxMaService.getRunService().getRunStepInfo(sessionKey, encryptedData, iv);
        for (WxMaRunStepInfo info : runStepInfo) {
            Long ts = info.getTimestamp() * 1000;
            Integer step = info.getStep();
            System.out.println(DateUtil.date(ts) + ",步数" + step);
        }

        return AjaxResult.ok();
    }

}
