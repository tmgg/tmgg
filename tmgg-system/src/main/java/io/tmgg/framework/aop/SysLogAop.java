
package io.tmgg.framework.aop;

import io.tmgg.modules.sys.service.SysOpLogService;
import io.tmgg.web.consts.CommonConstant;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import io.tmgg.lang.obj.AjaxResult;
import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 业务日志aop切面
 */
@Component
@Aspect
public class SysLogAop {

    @Resource
    SysOpLogService sysOpLogService;

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
        Subject subject = SecurityUtils.getSubject();
        String account = CommonConstant.UNKNOWN;
        if (subject != null ) {
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
    }

    /**
     * 操作发生异常记录日志
     *
     */
    @AfterThrowing(pointcut = "getLogPointCut()", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, Exception exception) {
        Subject subject = SecurityUtils.getSubject();
        String account = CommonConstant.UNKNOWN;
        if (subject != null ) {
            account = subject.getAccount();
        }
        //异步记录日志
        sysOpLogService.saveExceptionLog(account, joinPoint, exception);
    }


}
