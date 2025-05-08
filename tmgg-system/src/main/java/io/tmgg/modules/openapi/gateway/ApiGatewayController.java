package io.tmgg.modules.openapi.gateway;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.openapi.entity.OpenApiAccount;
import io.tmgg.modules.openapi.entity.OpenApiAccountResource;
import io.tmgg.modules.openapi.service.ApiAccountService;
import io.tmgg.modules.openapi.service.ApiResourceService;
import io.tmgg.modules.openapi.service.OpenApiAccountResourceService;
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


    private OpenApiAccountResourceService openApiAccountResourceService;

    private ApiResourceService resourceService;

    @PostMapping
    public AjaxResult process(
            @RequestHeader("x-action") String action,
            @RequestHeader("x-app-id") String appId,
            @RequestHeader("x-timestamp") long timestamp,
            @RequestHeader("x-signature") String signature,
            @RequestHeader("x-request-id") String requestId,
            @RequestParam Map<String,Object> params,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String data =  joinParams(params) ;


        Assert.hasText(data, "请求体不能为空");

        // 验证时间戳，与服务器时间差异不能超过x分钟
        long diffTime = (System.currentTimeMillis() - timestamp) / (1000 * 60); // 分钟
        Assert.state(Math.abs(diffTime) < TIME_DIFF_LIMIT, "时间戳差异大，差距" + diffTime + "分钟");


        OpenApiAccount account = apiAccountService.findOne(appId);
        Assert.notNull(account, "账号不存在");
        Assert.state(account.getEnable(), "账号已禁用");


        // 校验是否超期
        if (account.getEndTime() != null) {
            Assert.state(DateUtil.current() < account.getEndTime().getTime(), "已过有效期");
        }




        // 校验权限
        OpenApiAccountResource ar = openApiAccountResourceService.findByAccountAndAction(account, action);
        Assert.notNull(ar, "账号没有权限, action: " + action);
        Assert.state(ar.getEnable(), "您的权限已被禁用, action: " + action);

        Method method = apiResourceService.findMethodByAction(action);
        Assert.notNull(method, "接口不存在,接口：" + action);

        String clientIP = JakartaServletUtil.getClientIP(request);
        Assert.state(StrUtil.isEmpty(account.getAccessIp()) || account.getAccessIp().contains(clientIP), "IP访问限制,您的IP为" + clientIP);

        String appSecret = account.getAppSecret();

        // 校验签名
        this.checkSign(action, appId, timestamp, data, signature, appSecret);

        Object retValue = dispatch(params, method, request, response);

        return AjaxResult.ok().data(retValue);

    }

    private  Object dispatch(Map<String,Object> params, Method method, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
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

        if(e instanceof CodeException be){
            code = be.getCode();
            msg = be.getMessage();
        }

        return AjaxResult.err(msg).code(code);
    }


    /**
     * 校验签名
     */
    public void checkSign(String action, String appId, long timestamp, String data, String sign, String appSecret) {
        String calc = this.sign(action, appId, timestamp, data, appSecret);
        Assert.state(sign.equals(calc), "签名错误");
    }

    private String sign(String action, String appId, long timestamp, String postData, String appSecret) {
        StringBuilder sb = new StringBuilder();
        sb.append(action).append("\n");
        sb.append(appId).append("\n");
        sb.append(timestamp).append("\n");
        sb.append(postData);

        String signStr = sb.toString();
        log.debug("签名内容 {}", signStr);
        return SecureUtil.hmacSha256(appSecret).digestHex(signStr);
    }

    private String joinParams(Map<String, Object> params) {
        params = params == null ? new TreeMap<>() : new TreeMap<>(params);

        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            if (v != null) {
                sb.append(k).append("=").append(v).append("&");
            }
        });
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    @Resource
    private ApiAccountService apiAccountService;

    @Resource
    private ApiResourceService apiResourceService;
}
