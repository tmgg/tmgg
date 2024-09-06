package io.tmgg.weapp;

import io.tmgg.lang.RequestTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.weapp.entity.WeappUser;
import io.tmgg.weapp.service.WeappUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class WeappTool {

    public static String curAppId(){
        HttpServletRequest request = RequestTool.currentRequest();
        String appId = request.getHeader("appId");
        return appId;
    }
    public static String curUserId(HttpSession session){
       return (String) session.getAttribute("APP_USER_ID");
    }

    // TODO 缓存
    public static WeappUser curUser(HttpSession session){
        String userId = curUserId(session);
        if (userId != null){
            WeappUserService weappUserService = SpringTool.getBean(WeappUserService.class);
            return weappUserService.findOne(userId);
        }

        return  null;
    }
}
