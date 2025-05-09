
package io.tmgg.modules.sys.service;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.tmgg.event.SysConfigChangeEvent;
import io.tmgg.framework.dbconfig.DbValue;
import io.tmgg.framework.perm.PermissionService;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.IpAddressTool;
import io.tmgg.lang.UserAgentTool;
import io.tmgg.lang.dao.BaseService;
import io.tmgg.modules.sys.dao.SysOpLogDao;
import io.tmgg.modules.sys.entity.SysLog;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SysLogService extends BaseService<SysLog> implements Runnable {

    private static final ObjectMapper om = new ObjectMapper();
    static {
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    @Resource
    private SysOpLogDao dao;

    @Resource
    private PermissionService permissionService;

    /**
     * 待保存队列
     */
    private final List<SysLog> persistList = Collections.synchronizedList(new LinkedList<>());


    public void saveOperationLog(final String account, JoinPoint joinPoint, boolean success, final String msg) {
        if(!opLogEnable){
            return;
        }
        SysLog sysLog = newSysOpLog(HttpServletTool.getRequest(), joinPoint);

        sysLog.setAccount(account);
        sysLog.setSuccess(success);
        sysLog.setMessage(msg);

        Object params = getParams(joinPoint);
        if (params != null) {

            try {
                sysLog.setParam(om.writeValueAsString(params));
            } catch (JsonProcessingException e) {
                log.error("将参数序列化时异常",e);
            }
        }

        persistList.add(sysLog);
    }



    public void saveExceptionLog(final String account, JoinPoint joinPoint, Exception exception) {
        if(!opLogEnable){
            return;
        }
        SysLog sysLog = this.newSysOpLog(HttpServletTool.getRequest(), joinPoint);
        sysLog.setAccount(account);
        sysLog.setSuccess(false);
        sysLog.setMessage(Arrays.toString(exception.getStackTrace()));
        persistList.add(sysLog);
    }

    private  Object getParams(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        if (signature instanceof MethodSignature methodSignature) {
            Method method = methodSignature.getMethod();

            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if (parameter.getAnnotation(RequestBody.class) != null) {
                    return args[i];
                }

            }

        }

        return null;
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

        return sysLog;
    }

    private String parseModule(Object controller) {
        Class<?> cls = controller.getClass();
        String simpleName = cls.getSimpleName();
        String controllerName = simpleName.replace("Controller", "");

        String permLabel = permissionService.getPermLabel(StrUtil.lowerFirst(controllerName));
        if (permLabel != null) {
            return permLabel;
        }

        return simpleName;

    }


    @DbValue("sys.opLog.enable")
    boolean opLogEnable;


    private ScheduledExecutorService executorService;


    @PostConstruct
    public void init() {
        log.info("操作日志是否开启 {}", opLogEnable);
        if (opLogEnable) {
            executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleWithFixedDelay(this, 30 , 5, TimeUnit.SECONDS);
            log.info("启动异步保存操作日志线程 ");
        }else {
            persistList.clear();
            if(executorService !=null){
                executorService.shutdown();
                executorService = null;
            }
        }
    }

    @EventListener(SysConfigChangeEvent.class)
    public void onChange() {
        this.init();
    }

    @Override
    public void run() {
        try {
            if (persistList.isEmpty()) {
                return;
            }

            int batchSize = 1000;
            List<SysLog> batch = new ArrayList<>(batchSize);
            int size = NumberUtil.min(batchSize, persistList.size());
            for(int i = 0; i < size; i ++){
                SysLog sysLog = persistList.remove(0);
                batch.add(sysLog);
            }
            dao.saveAll(batch);
        } catch (Exception e) {
            log.info("保存日志失败", e);
        }

    }


}
