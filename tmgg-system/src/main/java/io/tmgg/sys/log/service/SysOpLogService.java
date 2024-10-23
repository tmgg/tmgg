
package io.tmgg.sys.log.service;

import io.tmgg.core.enums.LogSuccessStatusEnum;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.IpAddressTool;
import io.tmgg.lang.JoinPointTool;
import io.tmgg.lang.UserAgentTool;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.sys.dao.SysOpLogDao;
import io.tmgg.sys.log.entity.SysOpLog;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.context.requestno.RequestNoContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SysOpLogService extends BaseService<SysOpLog> {

    @Resource
    SysOpLogDao dao;

    public void saveLoginLog(final String account, final String success, final String failMessage) {
        HttpServletRequest request = HttpServletTool.getRequest();
        SysOpLog sysVisLog = newSysOpLog(request);


        sysVisLog.setName("登录");
        sysVisLog.setSuccess(success);
        sysVisLog.setAccount(account);

        if (LogSuccessStatusEnum.SUCCESS.getCode().equals(success)) {
            sysVisLog.setMessage("登录成功");
        }
        if (LogSuccessStatusEnum.FAIL.getCode().equals(success)) {
            sysVisLog.setMessage("登录失败:" + failMessage);
        }
        dao.save(sysVisLog);

    }

    /**
     * 登出日志
     */
    public void saveLogoutLog(final String account) {
        HttpServletRequest request = HttpServletTool.getRequest();
        SysOpLog sysVisLog = newSysOpLog(request);
                    sysVisLog.setName("登出");
                    sysVisLog.setSuccess(LogSuccessStatusEnum.SUCCESS.getCode());
                    sysVisLog.setMessage("登出成功");
                    sysVisLog.setAccount(account);
                    dao.save(sysVisLog);

    }


    public void saveOperationLog(final String account, JoinPoint joinPoint, final String result) {
        SysOpLog sysOpLog = newSysOpLog(HttpServletTool.getRequest());

                    String businessLog = parseBusinessLog(joinPoint);
                    fillCommonSysOpLog(sysOpLog, account, businessLog, joinPoint);
                    sysOpLog.setSuccess(LogSuccessStatusEnum.SUCCESS.getCode());
                    sysOpLog.setResult(result);
                    sysOpLog.setMessage(LogSuccessStatusEnum.SUCCESS.getMessage());
                    dao.save(sysOpLog);

    }


    public void saveExceptionLog(final String account, JoinPoint joinPoint, Exception exception) {
        SysOpLog sysOpLog = this.newSysOpLog(HttpServletTool.getRequest());
        String businessLog = parseBusinessLog(joinPoint);

                    fillCommonSysOpLog(sysOpLog, account, businessLog, joinPoint);
                    sysOpLog.setSuccess(LogSuccessStatusEnum.FAIL.getCode());
                    sysOpLog.setMessage(Arrays.toString(exception.getStackTrace()));
                    dao.save(sysOpLog);

    }




    private SysOpLog newSysOpLog(HttpServletRequest request) {
        String ip = IpAddressTool.getIp(request);

        String browser = UserAgentTool.getBrowser(request);
        String os = UserAgentTool.getOs(request);
        String url = request.getRequestURI();
        String method = request.getMethod();

        SysOpLog sysOpLog = new SysOpLog();
        sysOpLog.setIp(ip);
        sysOpLog.setLocation( IpAddressTool.getLocation(ip));
        sysOpLog.setBrowser(browser);
        sysOpLog.setOs(os);
        sysOpLog.setUrl(url);
        sysOpLog.setReqMethod(method);
        sysOpLog.setCreateTime(new Date());
        return sysOpLog;
    }


    private String parseBusinessLog(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        HasPermission businessLog = method.getAnnotation(HasPermission.class);
        if (businessLog == null) {
            return method.getName();
        }
        String logMsg = businessLog.value();

        Object controller = joinPoint.getTarget();
        HasPermission controllerLog = controller.getClass().getAnnotation(HasPermission.class);
        if (controllerLog != null) {
            logMsg = controllerLog.value() + "_" + logMsg;
        }
        return logMsg;
    }




    /**
     * 生成通用操作日志字段
     *

     *
     */
    private void fillCommonSysOpLog(SysOpLog sysOpLog, String account, String businessLog, JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();

        String methodName = joinPoint.getSignature().getName();

        String param = JoinPointTool.getArgsJsonString(joinPoint);

        sysOpLog.setName(businessLog);
        sysOpLog.setClassName(className);
        sysOpLog.setMethodName(methodName);
        sysOpLog.setParam(param);
        sysOpLog.setAccount(account);
    }


}
