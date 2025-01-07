
package io.tmgg.modules.sys.service;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.IpAddressTool;
import io.tmgg.lang.JoinPointTool;
import io.tmgg.lang.UserAgentTool;
import io.tmgg.lang.ann.Remark;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.sys.dao.SysOpLogDao;
import io.tmgg.modules.sys.entity.SysLog;
import io.tmgg.web.annotion.HasPermission;
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
    SysOpLogDao dao;


    public void saveOperationLog(final String account, JoinPoint joinPoint, boolean success, final String msg) {
        SysLog sysLog = newSysOpLog(HttpServletTool.getRequest());

        String businessLog = parseLogName(joinPoint);
        fillCommonSysOpLog(sysLog, account, businessLog, joinPoint);
        sysLog.setSuccess(success);
        sysLog.setMessage(msg);
        dao.saveAsync(sysLog);
    }

    public void saveExceptionLog(final String account, JoinPoint joinPoint, Exception exception) {
        SysLog sysLog = this.newSysOpLog(HttpServletTool.getRequest());
        String businessLog = parseLogName(joinPoint);

        fillCommonSysOpLog(sysLog, account, businessLog, joinPoint);
        sysLog.setSuccess(false);
        sysLog.setMessage(Arrays.toString(exception.getStackTrace()));
        dao.saveAsync(sysLog);

    }


    private SysLog newSysOpLog(HttpServletRequest request) {
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
        return sysLog;
    }


    private String parseLogName(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object controller = joinPoint.getTarget();

        String baseName = StrUtil.removeSuffix( controller.getClass().getSimpleName(), "Controller");
        String methodName = method.getName();

        Remark controllerRemark = controller.getClass().getAnnotation(Remark.class);
        if(controllerRemark != null){
            baseName = controllerRemark.value();
        }

        Remark methodRemark = method.getAnnotation(Remark.class);
        if(methodRemark != null){
            methodName = methodRemark.value();
        }

        return baseName + "-" + methodName;
    }


    /**
     * 生成通用操作日志字段
     */
    private void fillCommonSysOpLog(SysLog sysLog, String account, String businessLog, JoinPoint joinPoint) {
        String param = JoinPointTool.getArgsJsonString(joinPoint);
        sysLog.setName(businessLog);
        sysLog.setParam(param);
        sysLog.setAccount(account);
    }


}
