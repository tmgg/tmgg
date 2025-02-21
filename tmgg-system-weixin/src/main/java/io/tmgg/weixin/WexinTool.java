package io.tmgg.weixin;

import io.tmgg.lang.RequestTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.weixin.entity.WeixinUser;
import io.tmgg.weixin.service.WexinUserService;

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
            WexinUserService wexinUserService = SpringTool.getBean(WexinUserService.class);
            return wexinUserService.findOne(userId);
        }

        return  null;
    }
}
