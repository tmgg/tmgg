package io.tmgg.framework.interceptor;


import cn.hutool.core.util.ObjUtil;
import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.ResponseTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 认证校验
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        boolean canAccess = canAccess(request, handler);
        if (!canAccess) {
            log.warn("未登录用户尝试访问需要登录的资源 {}  {}", request.getMethod(), request.getRequestURI());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ResponseTool.responseExceptionError(response, HttpStatus.UNAUTHORIZED.value(), "未登录或登录过期，请重新登录");
            return false;
        }


        return true;
    }

    private boolean canAccess(HttpServletRequest request, Object handler) {
        String url = request.getRequestURI();
        if (url.contains("public")) {
            return true;
        }
        if (url.contains("login")) {
            return true;
        }

        if (handler instanceof ResourceHttpRequestHandler) {
            ResourceHttpRequestHandler rh = ((ResourceHttpRequestHandler) handler);
            log.debug("访问静态资源 {} {}", request.getMethod(), url);
            return true;
        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = ((HandlerMethod) handler);
            if (hm.hasMethodAnnotation(PublicRequest.class)) {
                return true;
            }
        }

        Boolean isLogin = (Boolean) request.getSession().getAttribute("isLogin");
        isLogin = ObjUtil.defaultIfNull(isLogin, false);

        return isLogin;
    }


}
