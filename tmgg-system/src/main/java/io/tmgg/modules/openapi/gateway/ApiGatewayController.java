package io.tmgg.modules.openapi.gateway;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.TableMap;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.HttpUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.openapi.ApiResource;
import io.tmgg.modules.openapi.entity.OpenApiAccount;
import io.tmgg.modules.openapi.service.ApiAccountService;
import io.tmgg.modules.openapi.service.ApiResourceService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@RestController
@RequestMapping("openApi/gateway")
public class ApiGatewayController {


    public static final int TIME_DIFF_LIMIT = 5;

    @PostMapping
    public AjaxResult process(
            @RequestHeader("x-action") String action,
            @RequestHeader("x-app-id") String appId,
            @RequestHeader("x-timestamp") long timestamp,
            @RequestHeader("x-signature") String signature,
            @RequestParam Map<String,Object> params,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String data =  joinParams(params) ;


        Assert.hasText(data, "请求体不能为空");

        // 验证时间戳，与服务器时间差异不能超过20分钟
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
        Assert.state(CollUtil.contains(account.getPerms(), action), "账号没有权限, uri: " + action);


        ApiResource resource = apiResourceService.findByMethod(action);
        Assert.notNull(resource, "接口不存在,接口：" + action);

        String clientIP = JakartaServletUtil.getClientIP(request);
        Assert.state(StrUtil.isEmpty(account.getAccessIp()) || account.getAccessIp().contains(clientIP), "IP访问限制,您的IP为" + clientIP);

        String appSecret = account.getAppSecret();

        // 校验签名
        this.checkSign(action, appId, timestamp, data, signature, appSecret);

        Object retValue = dispatch(params, resource, request, response);
        return AjaxResult.ok().data(JsonTool.toJson(retValue));

    }

    private  Object dispatch(Map<String,Object> params, ApiResource resource, HttpServletRequest request, HttpServletResponse response) throws IOException, IllegalAccessException, InvocationTargetException {
        Method method = resource.getMethod();
        Object[] paramValues = ArgumentResolver.resolve(method, params, request, response);
        Object retValue = null;
        if (paramValues.length == 0) {
            retValue = method.invoke(resource.getBean());
        } else {
            retValue = method.invoke(resource.getBean(), paramValues);
        }

        Assert.notNull(retValue, "接口必须有返回值");
        retValue = JsonTool.toJsonQuietly(retValue);
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


        Map<Class<? extends Exception>, String> codes = new TableMap<>();
        codes.put(CryptoException.class, 2001 + "签名或加密错误，请检查秘钥是否正确");

        for (Map.Entry<Class<? extends Exception>, String> entry : codes.entrySet()) {
            Class<? extends Exception> cls = entry.getKey();
            String codeMsg = entry.getValue();
            if (cls.isAssignableFrom(e.getClass())) {
                msg = codeMsg.substring(4);
                code = Integer.parseInt(codeMsg.substring(0, 4));
            }
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
