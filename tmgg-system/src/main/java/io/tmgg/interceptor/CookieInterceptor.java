package io.tmgg.interceptor;


import io.tmgg.lang.ResponseTool;
import io.tmgg.lang.ann.PublicApi;
import io.tmgg.web.token.TokenManger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 认证校验
 */
@Slf4j
@Component
public class CookieInterceptor implements HandlerInterceptor {


    @Resource
    TokenManger tokenManger;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        boolean canAccess = canAccess(request, response, handler);
        if (!canAccess) {
            log.warn("尝试访问未授权资源 {}  {}", request.getMethod(), request.getRequestURI());
        }
        return canAccess;
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

        String token = tokenManger.getTokenFromRequest(request);


        //校验token，错误则抛异常
        try {
            tokenManger.validate(token);

            // 放到session中，方便后端页面直接访问
            HttpSession session = request.getSession();
            String sessionToken = (String) session.getAttribute(TokenManger.SESSION_PARAM);
            log.trace("sessionId={}, token是否存在 {}", session.getId(), sessionToken != null);
            if(sessionToken == null){
                session.setAttribute(TokenManger.SESSION_PARAM, token);
                log.trace("设置sessionToken, sessionId={}",session.getId());
            }
        }catch (Exception e){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ResponseTool.responseExceptionError(response, HttpStatus.UNAUTHORIZED.value(), e.getMessage() +",url=" + url);
            return false;
        }



        return true;
    }



}
