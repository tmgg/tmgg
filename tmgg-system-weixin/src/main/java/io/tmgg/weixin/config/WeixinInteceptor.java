package io.tmgg.weixin.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import io.tmgg.weixin.WexinTool;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 多个小程序的情况下
 */
@Component
public class WeixinInteceptor implements HandlerInterceptor {
    @Resource
    WxMaService wxMaService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String appId = WexinTool.curAppId();
        if(appId != null){
            // 设置当前线程的appId
            if (!wxMaService.switchover(appId)) {
                throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appId));
            }
        }


        return true;
    }
}
