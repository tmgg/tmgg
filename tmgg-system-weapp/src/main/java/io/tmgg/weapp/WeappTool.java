package io.tmgg.weapp;

import io.tmgg.lang.RequestTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.weapp.entity.WeappUser;
import io.tmgg.weapp.service.WeappUserService;
import io.tmgg.web.token.TokenManger;

import jakarta.servlet.http.HttpServletRequest;

public class WeappTool {

    public static String curAppId(){
        HttpServletRequest request = RequestTool.currentRequest();
        String appId = request.getHeader("appId");
        return appId;
    }
    public static String curUserId(){
        TokenManger tm = SpringTool.getBean(TokenManger.class);
        String token = tm.getTokenFromRequest(RequestTool.currentRequest(),true);
        String userId = tm.validate(token);

        return userId;
    }

    // TODO 缓存
    public static WeappUser curUser(){
        String uid = curUserId();

        if (uid != null){

            WeappUserService weappUserService = SpringTool.getBean(WeappUserService.class);
            return weappUserService.findOne(uid);
        }

        return  null;
    }
}
