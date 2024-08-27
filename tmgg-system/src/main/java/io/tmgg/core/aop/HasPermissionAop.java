
package io.tmgg.core.aop;

import io.tmgg.core.log.LogManager;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.consts.AopSortConstant;
import io.tmgg.web.enums.AdminType;
import io.tmgg.web.exception.PermissionException;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 权限过滤Aop切面
 */
@Aspect
@Slf4j
@Order(AopSortConstant.PERMISSION_AOP)
@Component
@ConditionalOnProperty(name = "tmgg.system.enable-permission-aop", havingValue = "true", matchIfMissing = true)
public class HasPermissionAop {

    /**
     * 执行权限过滤
     */
    @Before("@annotation(io.tmgg.web.annotion.HasPermission)")
    public void doPermission(JoinPoint joinPoint) {
        // 如果是超级管理员，直接放过权限校验
        Subject me = SecurityUtils.getSubject();
        boolean isSuperAdmin = me.getAdminType() == AdminType.SUPER_ADMIN;
        if (isSuperAdmin) {
            return;
        }

        // 如果不是超级管理员，则开始进行权限校验
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        HasPermission permission = method.getAnnotation(HasPermission.class);


        // 首先校验当前用户有没有 当前请求requestUri的权限
        HttpServletRequest request = HttpServletTool.getRequest();
        String uri = request.getRequestURI();


        boolean hasUriPermission = hasPermission(me, permission.value(), uri);
        if (!hasUriPermission) {
            PermissionException exception = new PermissionException("无权限" + permission.value());
            this.executeNoPermissionExceptionLog(joinPoint, exception);
            throw exception;
        }
    }


    private boolean hasPermission(Subject me, String permission, String uri) {
        if (StrUtil.isNotEmpty(permission)) {
            return me.hasPermission(permission);
        }
        return me.hasPermission(uri);
    }


    /**
     * 记录无权限异常日志
     */
    private void executeNoPermissionExceptionLog(JoinPoint joinPoint, Exception exception) {
        //异步记录日志
        if(SecurityUtils.getSubject().isAuthenticated()){
            LogManager.me().saveExceptionLog(SecurityUtils.getSubject().getAccount(), joinPoint, exception);

        }
    }

}
