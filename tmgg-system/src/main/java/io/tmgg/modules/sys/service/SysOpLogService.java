
package io.tmgg.modules.sys.service;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.IpAddressTool;
import io.tmgg.lang.JoinPointTool;
import io.tmgg.lang.UserAgentTool;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.sys.dao.SysOpLogDao;
import io.tmgg.modules.sys.entity.SysLog;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.framework.perm.PermissionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SysOpLogService extends BaseService<SysLog> implements Runnable {

    @Resource
    private SysOpLogDao dao;

    @Resource
    private PermissionService permissionService;

    private final LinkedList<SysLog> logsPendingSave = new LinkedList<>();




    public void saveOperationLog(final String account, JoinPoint joinPoint, boolean success, final String msg) {
        SysLog sysLog = newSysOpLog(HttpServletTool.getRequest(), joinPoint);

        sysLog.setAccount(account);
        sysLog.setSuccess(success);
        sysLog.setMessage(msg);

        logsPendingSave.add(sysLog);
    }

    public void saveExceptionLog(final String account, JoinPoint joinPoint, Exception exception) {
        SysLog sysLog = this.newSysOpLog(HttpServletTool.getRequest(), joinPoint);
        sysLog.setAccount(account);
        sysLog.setSuccess(false);
        sysLog.setMessage(Arrays.toString(exception.getStackTrace()));
        logsPendingSave.add(sysLog);
    }


    private SysLog newSysOpLog(HttpServletRequest request, JoinPoint joinPoint) {
        String ip = IpAddressTool.getIp(request);
        String browser = UserAgentTool.getBrowser(request);
        String os = UserAgentTool.getOs(request);
        String url = request.getRequestURI();

        SysLog sysLog = new SysLog();
        sysLog.setIp(ip);
        sysLog.setLocation(IpAddressTool.getLocation(ip));
        sysLog.setBrowser(browser);
        sysLog.setOs(os);
        sysLog.setUrl(url);
        sysLog.setCreateTime(new Date());


        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object controller = joinPoint.getTarget();
        HasPermission methodAnn = method.getAnnotation(HasPermission.class);



        String perm = permissionService.parsePerm(methodAnn, url);
        String name = permissionService.parsePermLabel(perm, methodAnn);


        sysLog.setName(name);
        sysLog.setModule(parseModule(controller));
        String param = JoinPointTool.getArgsJsonString(joinPoint);
        sysLog.setParam(param);

        return sysLog;
    }

    private String parseModule(Object controller){
        Class<?> cls = controller.getClass();
        String simpleName = cls.getSimpleName();
        String controllerName = simpleName.replace("Controller", "");

        String permLabel = permissionService.getPermLabel(StrUtil.lowerFirst(controllerName));
        if(permLabel != null){
            return permLabel;
        }

        return simpleName;

    }
    private  final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
    @PostConstruct
    public void post(){
        executorService.scheduleWithFixedDelay(this, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        if (logsPendingSave.isEmpty()) {
            return;
        }
        List<SysLog> result = dao.saveAll(logsPendingSave);

        // 移除保存成功的日志 （不使用clear是为了保持）
        logsPendingSave.removeAll(result);
    }
}
