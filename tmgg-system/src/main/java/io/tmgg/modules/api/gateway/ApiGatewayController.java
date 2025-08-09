package io.tmgg.modules.api.gateway;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.api.ApiSignTool;
import io.tmgg.modules.api.entity.ApiAccount;
import io.tmgg.modules.api.entity.ApiAccountResource;
import io.tmgg.modules.api.service.ApiAccessLogService;
import io.tmgg.modules.api.service.ApiAccountResourceService;
import io.tmgg.modules.api.service.ApiAccountService;
import io.tmgg.modules.api.service.ApiResourceService;
import io.tmgg.web.CodeException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@RestController
@RequestMapping("openApi/gateway")
public class ApiGatewayController {


    public static final int TIME_DIFF_LIMIT = 5;


    @Resource
    private ApiAccountResourceService accountResourceService;


    @Resource
    private ApiAccessLogService accessLogService;


    @PostMapping("{path}")
    public AjaxResult process(
            @PathVariable String path,
            @RequestHeader String appId,
            @RequestHeader long timestamp,
            @RequestHeader String sign,
            @RequestParam Map<String, Object> params,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        long startTime = System.currentTimeMillis();

        // 验证时间戳，与服务器时间差异不能超过x分钟
        long diffTime = (System.currentTimeMillis() - timestamp) / 1000;
        Assert.state(Math.abs(diffTime) < TIME_DIFF_LIMIT, "请求时间戳与服务器时间差异过大（" + diffTime + "秒）");


        ApiAccount account = apiAccountService.findOne(appId);
        Assert.notNull(account, "账号不存在");
        Assert.state(account.getEnable(), "账号已禁用");


        // 校验是否超期
        if (account.getEndTime() != null) {
            Assert.state(DateUtil.current() < account.getEndTime().getTime(), "已过有效期");
        }



        // 校验签名
        String appSecret = account.getAppSecret();
        String calcSign = ApiSignTool.sign(appId, appSecret, timestamp);
        Assert.state(sign.equals(calcSign), "签名错误");


        // 校验权限
        ApiAccountResource ar = accountResourceService.findByAccountAndPath(account, path);
        Assert.notNull(ar, "账号没有权限, uri: " + path);
        Assert.state(ar.getEnable(), "您的权限已被禁用, path: " + path);

        Method method = apiResourceService.findMethodByAction(path);
        Assert.notNull(method, "接口不存在,接口：" + path);

        String clientIP = JakartaServletUtil.getClientIP(request);
        Assert.state(StrUtil.isEmpty(account.getAccessIp()) || account.getAccessIp().contains(clientIP), "IP访问限制,您的IP为" + clientIP);


        Object retValue = dispatch(params, method, request, response);


        // 保存日志
        String ip = JakartaServletUtil.getClientIP(request);
        long time = System.currentTimeMillis() - startTime;
        accessLogService.add(timestamp,account, ar.getResource(), params, retValue, ip, time);


        return AjaxResult.ok().data(retValue);

    }

    private Object dispatch(Map<String, Object> params, Method method, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
        Object[] paramValues = ArgumentResolver.resolve(method, params, request, response);

        Class<?> declaringClass = method.getDeclaringClass();
        Object bean = SpringTool.getBean(declaringClass);

        Object retValue = method.invoke(bean, paramValues);

        Assert.notNull(retValue, "接口必须有返回值");
        return retValue;
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult parseException(Throwable e) {
        e.printStackTrace();

        if (e instanceof InvocationTargetException ite) {
            e = ite.getTargetException();
        }


        int code = AjaxResult.FAILURE;
        String msg = e.getMessage();

        if (e instanceof CodeException be) {
            code = be.getCode();
            msg = be.getMessage();
        }

        return AjaxResult.err(msg).code(code);
    }




    @Resource
    private ApiAccountService apiAccountService;

    @Resource
    private ApiResourceService apiResourceService;
}
