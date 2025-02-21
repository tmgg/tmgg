package io.tmgg.modules.api.gateway;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.TableMap;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.extra.servlet.JakartaServletUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.modules.api.Api;
import io.tmgg.modules.api.ApiResource;
import io.tmgg.modules.api.ApiResult;
import io.tmgg.modules.api.entity.ApiAccount;
import io.tmgg.modules.api.service.ApiAccountService;
import io.tmgg.modules.api.service.ApiResourceService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/gateway")
public class ApiGatewayController {


    public static final int TIME_DIFF_LIMIT = 5;

    @PostMapping
    public ApiResult process(HttpServletRequest request, HttpServletResponse response,
                          @RequestHeader("x-action") String action,
                          @RequestHeader("x-app-id") String appId,
                          @RequestHeader("x-timestamp") long timestamp,
                          @RequestHeader("x-signature") String signature,
                          String data) throws Exception {

        Assert.hasText(data, "请求体不能为空");

        // 验证时间戳，与服务器时间差异不能超过20分钟
        long diffTime = (System.currentTimeMillis() - timestamp) / (1000 * 60); // 分钟
        Assert.state(Math.abs(diffTime) < TIME_DIFF_LIMIT, "时间戳差异大，差距" + diffTime + "分钟");


        ApiAccount account = apiAccountService.findOne(appId);
        Assert.notNull(account, "账号不存在");
        Assert.state(account.getEnable(), "账号已禁用");


        // 校验是否超期
        Assert.state(account.getEndTime() == null || DateUtil.current() > account.getEndTime().getTime(), "账号已超过有效期");

        // 校验权限
        Assert.state(CollUtil.contains(account.getPerms(), action), "账号没有权限, uri: " + action);


        ApiResource resource = apiResourceService.findByMethod(action);
        Assert.notNull(resource, "接口不存在,接口：" + action);
        Api api = resource.getApi();

        String clientIP = JakartaServletUtil.getClientIP(request);
        Assert.state(StrUtil.isEmpty(account.getAccessIp()) || account.getAccessIp().contains(clientIP), "IP限制,IP：" + clientIP);

        // 解密
        String appSecret = account.getAppSecret();
        AES aes = SecureUtil.aes(appSecret.getBytes());
        if (api.encrypt()) {
            System.out.println(data);
            data = aes.decryptStr(data);
        }


        // 校验签名
        this.checkSign(action, appId, timestamp, data, signature, appSecret);

        Map<String, Object> params = JsonTool.jsonToMap(data);
        Method method = resource.getMethod();
        Object[] paramValues = ArgumentResolver.resolve(method, params, request, response);
        Object retValue = null;
        if (paramValues.length == 0) {
            retValue = method.invoke(resource.getBean());
        } else {
            retValue = method.invoke(resource.getBean(), paramValues);
        }

        Assert.notNull(retValue, "接口必须有返回值");
        if (api.encrypt()) {
            String res = JsonTool.toJsonQuietly(retValue);
            retValue = aes.encryptBase64(res);
        }
        return new ApiResult(1000, null, JsonTool.toPrettyJsonQuietly(retValue));
    }


    /**
     * 校验签名
     */
    public void checkSign(String uri, String appKey, long timestamp, String data, String sign, String secret) {
        String calcSign = SecureUtil.hmacSha256(secret).digestBase64(uri + appKey + timestamp + data, false);

        Assert.state(sign.equals(calcSign), "签名错误");
    }


    @ExceptionHandler
    @ResponseBody
    private ApiResult responseError(Exception e, HttpServletResponse response) {
        e.printStackTrace();
        int code = -1;
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

        return new ApiResult(code, msg);
    }

    @Resource
    private ApiAccountService apiAccountService;

    @Resource
    private ApiResourceService apiResourceService;
}
