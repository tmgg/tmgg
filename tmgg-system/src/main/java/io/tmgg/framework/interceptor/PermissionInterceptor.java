package io.tmgg.framework.interceptor;


import io.tmgg.lang.ResponseTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.perm.PermissionService;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 检查某个用户是否有功能权限
 */
@Component
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {

    @Resource
    PermissionService permissionService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod hm) {
            String perm = permissionService.parsePerm(hm, request.getRequestURI());
            if (perm == null) {
                return true; // 没有权限限制
            }

            Subject subject = SecurityUtils.getSubject();
            if(subject != null){
                boolean ok = subject.hasPermission(perm);
                if(!ok){
                    ResponseTool.responseJson(response, AjaxResult.err().msg("对不起，您的权限不足。 [" + perm + "]"));
                }
                return ok;
            }

        }

        return true;



    }




}
