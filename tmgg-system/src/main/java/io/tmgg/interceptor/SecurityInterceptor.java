package io.tmgg.interceptor;


import cn.hutool.core.util.ObjUtil;
import io.tmgg.lang.ann.PublicApi;
import io.tmgg.lang.ResponseTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 认证校验
 */
@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        boolean canAccess = canAccess(request, response, handler);
        if (!canAccess) {
            log.warn("尝试访问未授权资源 {}  {}", request.getMethod(), request.getRequestURI());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ResponseTool.responseExceptionError(response, HttpStatus.UNAUTHORIZED.value(), "未认证");
            return false;
        }


        return true;
    }

    private boolean canAccess(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String url = request.getRequestURI();
        if (url.contains("public")) {
            return true;
        }
        if (url.contains("login")) {
            return true;
        }

        if(handler instanceof ResourceHttpRequestHandler ){
            ResourceHttpRequestHandler rh =  ((ResourceHttpRequestHandler) handler);
            log.info("访问静态资源 {} {}" , request.getMethod(), url);
            return true;
        }
        if(handler instanceof HandlerMethod){
            HandlerMethod hm =((HandlerMethod) handler);
            if(hm.hasMethodAnnotation(PublicApi.class)){
                return true;
            }
        }

        Boolean isLogin = (Boolean) request.getSession().getAttribute("isLogin");
        isLogin = ObjUtil.defaultIfNull(isLogin, false);

        return  isLogin;
    }



}
