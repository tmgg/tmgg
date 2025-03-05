
package io.tmgg.modules.sys.service;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.IpAddressTool;
import io.tmgg.lang.JoinPointTool;
import io.tmgg.lang.UserAgentTool;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.sys.dao.SysMenuDao;
import io.tmgg.modules.sys.dao.SysOpLogDao;
import io.tmgg.modules.sys.entity.SysLog;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.framework.perm.PermissionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Service
public class SysOpLogService extends BaseService<SysLog> {

    @Resource
    private SysOpLogDao dao;

    @Resource
    private SysMenuDao sysMenuDao;

    @Resource
    private PermissionService permissionService;


    public void saveOperationLog(final String account, JoinPoint joinPoint, boolean success, final String msg) {
        SysLog sysLog = newSysOpLog(HttpServletTool.getRequest(), joinPoint);
        sysLog.setAccount(account);
        sysLog.setSuccess(success);
        sysLog.setMessage(msg);
        dao.saveAsync(sysLog);
    }

    public void saveExceptionLog(final String account, JoinPoint joinPoint, Exception exception) {
        SysLog sysLog = this.newSysOpLog(HttpServletTool.getRequest(), joinPoint);
        sysLog.setAccount(account);
        sysLog.setSuccess(false);
        sysLog.setMessage(Arrays.toString(exception.getStackTrace()));
        dao.saveAsync(sysLog);

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


        String name = method.getName();
        String module = controller.getClass().getSimpleName();

        if (StrUtil.isNotEmpty(methodAnn.label())) {
            name = methodAnn.label();
        }
        if (StrUtil.isEmpty(name)) {
            // TODO
        }

        sysLog.setName(name);
        sysLog.setModule(module);


        String param = JoinPointTool.getArgsJsonString(joinPoint);
        sysLog.setParam(param);


        return sysLog;
    }


}
