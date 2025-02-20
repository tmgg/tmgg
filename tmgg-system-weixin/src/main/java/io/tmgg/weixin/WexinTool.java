package io.tmgg.weixin;

import io.tmgg.lang.RequestTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.weixin.entity.WeixinUser;
import io.tmgg.weixin.service.WeappUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class WexinTool {

    public static String curAppId(){
        HttpServletRequest request = RequestTool.currentRequest();
        String appId = request.getHeader("appId");
        return appId;
    }
    public static String curUserId(HttpSession session){
       return (String) session.getAttribute("APP_USER_ID");
    }

    // TODO 缓存
    public static WeixinUser curUser(HttpSession session){
        String userId = curUserId(session);
        if (userId != null){
            WeappUserService weappUserService = SpringTool.getBean(WeappUserService.class);
            return weappUserService.findOne(userId);
        }

        return  null;
    }
}
