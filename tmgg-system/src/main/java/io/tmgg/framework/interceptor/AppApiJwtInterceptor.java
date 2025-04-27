package io.tmgg.framework.interceptor;

import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.ann.PublicRequest;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.service.JwtService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class AppApiJwtInterceptor implements HandlerInterceptor {

    public static final String APP_API = "/app-api";
    public static final String PATTERN = APP_API + "/**";


    @Resource
    private JwtService jwtService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        if(handler instanceof HandlerMethod m){
            if(m.hasMethodAnnotation(PublicRequest.class)){
                return true;
            }
        }
        String token = request.getHeader("Authorization");
        if (token == null) {
            renderError(4010, "登录凭证不存在", response);
            return false;
        }

        token = jwtService.getToken(request);
        try {
            jwtService.verify(token);
        } catch (Exception e) {
            log.error("验证jwt失败： {}", e.getMessage());
            renderError(4011, "凭证错误", response);
            return false;
        }

        return true;
    }


    public void renderError(int code, String msg, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        AjaxResult r = AjaxResult.err().code(code).msg(msg);
        writer.write(JsonTool.toJson(r));
        writer.flush();
    }


}
