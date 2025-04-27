
package io.tmgg.framework.aop;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.service.SysLogService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.consts.CommonConstant;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 业务日志aop切面
 */
@Slf4j
@Component
@Aspect
public class SysLogAop {

    @Resource
    SysLogService sysOpLogService;


    /**
     * 日志切入点
     */
    @Pointcut("@annotation(io.tmgg.web.annotion.HasPermission)")
    private void getLogPointCut() {
    }

    /**
     * 操作成功返回结果记录日志
     */
    @AfterReturning(pointcut = "getLogPointCut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        log.trace("日志切面开始 ");
        long startTime = System.currentTimeMillis();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        HasPermission ann = method.getAnnotation(HasPermission.class);
        if (!ann.log()) {
            return;
        }

        Subject subject = SecurityUtils.getSubject();
        String account = CommonConstant.UNKNOWN;
        if (subject != null) {
            account = subject.getAccount() + " " + subject.getName();
        }
        String msg = "操作成功";
        boolean success = true;
        if (result instanceof AjaxResult r) {
            success = r.isSuccess();
            msg = r.getMessage();
        }
        //异步记录日志
        sysOpLogService.saveOperationLog(account, joinPoint, success, msg);

        log.trace("日志切面结束 {}毫秒", System.currentTimeMillis() - startTime);
    }

    /**
     * 操作发生异常记录日志
     */
    @AfterThrowing(pointcut = "getLogPointCut()", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, Exception exception) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        HasPermission ann = method.getAnnotation(HasPermission.class);
        if (!ann.log()) {
            return;
        }

        Subject subject = SecurityUtils.getSubject();
        String account = CommonConstant.UNKNOWN;
        if (subject != null) {
            account = subject.getAccount();
        }
        //异步记录日志
        sysOpLogService.saveExceptionLog(account, joinPoint, exception);
    }


}
