
package io.tmgg.core.aop;

import io.tmgg.core.log.LogManager;
import io.tmgg.web.SystemException;
import io.tmgg.web.annotion.HasRole;
import io.tmgg.web.consts.AopSortConstant;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 权限过滤Aop切面
 */
@Aspect
@Slf4j
@Order(AopSortConstant.PERMISSION_AOP)
@Component
public class HasRoleAop {


    @Before("@annotation(io.tmgg.web.annotion.HasRole) || @within(io.tmgg.web.annotion.HasRole)")
    public void doPermission(JoinPoint joinPoint) {
        Subject me = SecurityUtils.getSubject();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        HasRole ann = methodSignature.getMethod().getAnnotation(HasRole.class);

        if (ann == null) { // 类上的注解
            ann = joinPoint.getTarget().getClass().getAnnotation(HasRole.class);
        }

        if (!me.hasRole(ann.value())) {
            SystemException e = new SystemException(401, "权限限制，角色：" + ann.value());
            this.executeNoPermissionExceptionLog(joinPoint, e);
            throw e;
        }
    }


    private void executeNoPermissionExceptionLog(JoinPoint joinPoint, Exception exception) {
        //异步记录日志
        if (SecurityUtils.getSubject()!= null) {
            LogManager.me().saveExceptionLog(SecurityUtils.getSubject().getAccount(), joinPoint, exception);
        }
    }

}
