package io.tmgg.weixin.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import io.tmgg.weixin.WexinTool;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class WeappInteceptor implements HandlerInterceptor {
    @Resource
    WxMaService wxMaService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String appId = WexinTool.curAppId();
        if(appId != null){
            if (!wxMaService.switchover(appId)) {
                throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appId));
            }
        }


        return true;
    }
}
