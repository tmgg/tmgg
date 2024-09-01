
package io.tmgg.core.log;

import io.tmgg.core.enums.LogSuccessStatusEnum;
import io.tmgg.lang.*;
import io.tmgg.lang.IpAddressTool;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.context.requestno.RequestNoContext;
import io.tmgg.sys.log.entity.SysOpLog;
import io.tmgg.sys.log.entity.SysVisLog;
import io.tmgg.sys.log.service.SysOpLogService;
import io.tmgg.sys.log.service.SysVisLogService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 日志管理器
 */
@Slf4j
@Component
public class LogManager {

    public static LogManager me() {
        LogManager bean = SpringUtil.getBean(LogManager.class);
        return bean;
    }

    /**
     * 登录日志
     */
    public void saveLoginLog(final String account, final String success, final String failMessage) {
        HttpServletRequest request = HttpServletTool.getRequest();
        SysVisLog sysVisLog = newVisLog(request);

        runTask(new TimerTask() {
            @Override
            public void run() {
                try {
                    sysVisLog.setLocation( IpAddressTool.getLocation(sysVisLog.getIp()));

                    sysVisLog.setName("登入");
                    sysVisLog.setSuccess(success);
                    sysVisLog.setVisTime(LocalDateTime.now());
                    sysVisLog.setAccount(account);

                    if (LogSuccessStatusEnum.SUCCESS.getCode().equals(success)) {
                        sysVisLog.setMessage("登录成功");
                    }
                    if (LogSuccessStatusEnum.FAIL.getCode().equals(success)) {
                        sysVisLog.setMessage("登录失败:" + failMessage);
                    }
                    sysVisLogService.save(sysVisLog);
                } catch (Exception e) {
                    log.error(">>> 创建登录日志异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
                }
            }
        });
    }

    /**
     * 登出日志
     */
    public void saveLogoutLog(final String account) {
        HttpServletRequest request = HttpServletTool.getRequest();
        SysVisLog sysVisLog = newVisLog(request);
        runTask(new TimerTask() {
            @Override
            public void run() {
                try {
                    sysVisLog.setLocation( IpAddressTool.getLocation(sysVisLog.getIp()));

                    sysVisLog.setName("登出");
                    sysVisLog.setSuccess(LogSuccessStatusEnum.SUCCESS.getCode());
                    sysVisLog.setMessage("登出成功");
                    sysVisLog.setVisTime(LocalDateTime.now());
                    sysVisLog.setAccount(account);
                    sysVisLogService.save(sysVisLog);
                } catch (Exception e) {
                    log.error(">>> 创建退出日志异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
                }
            }
        });
    }

    /**
     * 操作日志
     */
    public void saveOperationLog(final String account, JoinPoint joinPoint, final String result) {
        SysOpLog sysOpLog = newSysOpLog(HttpServletTool.getRequest());
        runTask(new TimerTask() {
            @Override
            public void run() {
                try {
                    String ip = sysOpLog.getIp();
                    sysOpLog.setLocation( IpAddressTool.getLocation(ip));
                    String businessLog = parseBusinessLog(joinPoint);
                    fillCommonSysOpLog(sysOpLog, account, businessLog, joinPoint);
                    sysOpLog.setSuccess(LogSuccessStatusEnum.SUCCESS.getCode());
                    sysOpLog.setResult(result);
                    sysOpLog.setMessage(LogSuccessStatusEnum.SUCCESS.getMessage());
                    sysOpLogService.save(sysOpLog);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(">>> 创建操作日志异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
                }
            }
        });
    }

    /**
     * 异常日志
     */
    public void saveExceptionLog(final String account, JoinPoint joinPoint, Exception exception) {
        SysOpLog sysOpLog = this.newSysOpLog(HttpServletTool.getRequest());
        String businessLog = parseBusinessLog(joinPoint);
        runTask(new TimerTask() {
            @Override
            public void run() {
                try {
                    fillCommonSysOpLog(sysOpLog, account, businessLog, joinPoint);
                    sysOpLog.setSuccess(LogSuccessStatusEnum.FAIL.getCode());
                    sysOpLog.setMessage(Arrays.toString(exception.getStackTrace()));
                    sysOpLogService.save(sysOpLog);
                } catch (Exception e) {
                    log.error(">>> 创建异常日志异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
                }
            }
        });
    }

    /**
     * 构建基础访问日志
     *

 *
     */
    private SysVisLog newVisLog(HttpServletRequest request) {
            String ip = IpAddressTool.getIp(request);

            String browser = UserAgentTool.getBrowser(request);
            String os = UserAgentTool.getOs(request);

            SysVisLog sysVisLog = new SysVisLog();
            sysVisLog.setIp(ip);

            sysVisLog.setBrowser(browser);
            sysVisLog.setOs(os);
            return sysVisLog;
    }

    /**
     * 构建基础操作日志
     */
    private SysOpLog newSysOpLog(HttpServletRequest request) {
            String ip = IpAddressTool.getIp(request);

            String browser = UserAgentTool.getBrowser(request);
            String os = UserAgentTool.getOs(request);
            String url = request.getRequestURI();
            String method = request.getMethod();

            SysOpLog sysOpLog = new SysOpLog();
            sysOpLog.setIp(ip);

            sysOpLog.setBrowser(browser);
            sysOpLog.setOs(os);
            sysOpLog.setUrl(url);
            sysOpLog.setReqMethod(method);
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
        sysOpLog.setOpTime(LocalDateTime.now());
        sysOpLog.setAccount(account);
    }





    /**
     * 异步执行
     */
    private void runTask(TimerTask task) {
        //日志记录操作延时
        int operateDelayTime = 10;
        EXECUTOR.schedule(task, operateDelayTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 异步操作记录日志的线程池
     */
    private ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(10, new ScheduledExecutorFactoryBean());


    @Resource
    private SysVisLogService sysVisLogService;
    @Resource
    private SysOpLogService sysOpLogService;


}
