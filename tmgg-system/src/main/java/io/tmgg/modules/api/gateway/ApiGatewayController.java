package io.tmgg.modules.api.gateway;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.extra.servlet.JakartaServletUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.modules.api.ApiResource;
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
    public Object process(HttpServletRequest request, HttpServletResponse response,
                          @RequestHeader("X-URI") String uri,
                          @RequestHeader("X-APP-KEY") String appKey,
                          @RequestHeader("X-TIMESTAMP") long timestamp,
                          @RequestHeader("X-SIGNATURE") String signature,
                          String data) throws Exception {

        // 验证时间戳，与服务器时间差异不能超过20分钟
        long diffTime = (System.currentTimeMillis() - timestamp) / (1000 * 60); // 分钟
        Assert.state(Math.abs(diffTime) < TIME_DIFF_LIMIT, "时间戳差异大，差距" + diffTime + "分钟");


        ApiAccount account = apiAccountService.findByAppKey(appKey);
        Assert.notNull(account, "账号不存在");
        Assert.state(account.getEnable(), "账号已禁用");


        // 校验是否超期
        Assert.state(account.getEndTime() == null || DateUtil.current() > account.getEndTime().getTime(), "账号已超过有效期");

        // 校验权限
        Assert.state(CollUtil.contains(account.getPerms(), uri), "账号没有权限, uri: " + uri);


        ApiResource resource = apiResourceService.findByMethod(uri);
        Assert.notNull(resource, "接口不存在,接口：" + uri);

        String clientIP = JakartaServletUtil.getClientIP(request);
        Assert.state(CollectionUtil.isEmpty(account.getAccessIp()) || account.getAccessIp().contains(clientIP), "IP限制,IP：" + clientIP);


        // 解密
        AES aes = SecureUtil.aes(account.getAppSecret().getBytes());
        if (resource.getApi().encrypt()) {
            if (StrUtil.isNotEmpty(data)) {
                data = aes.decryptStr(data);
            }
        }


        // 校验签名
        this.checkSign(uri, appKey, timestamp, data, signature, account.getPublicKey());

        Map<String, Object> params = JsonTool.jsonToMap(data);
        Method method = resource.getMethod();
        Object[] paramValues = ArgumentResolver.resolve(method, params, request, response);
        Object retValue = null;
        if (paramValues.length == 0) {
            retValue = method.invoke(resource.getBean());
        } else {
            retValue = method.invoke(resource.getBean(), paramValues);
        }

        Assert.notNull(retValue,"接口必须有返回值");
        if (resource.getApi().encrypt()) {
            String res = JsonTool.toJsonQuietly(retValue);
            retValue = aes.encryptHex(res);
        }

        return retValue;
    }


    /**
     * 校验签名
     */
    public void checkSign(String uri, String appKey, long timestamp, String data, String sign, String publicKey) {
        String md5 = SecureUtil.md5(uri + appKey + timestamp + data);
        RSA rsa = SecureUtil.rsa(null, publicKey);
        String md5Calc = rsa.decryptStr(sign, KeyType.PublicKey);

        Assert.state(md5Calc.equals(md5), "签名错误");
    }


    @ExceptionHandler
    @ResponseBody
    private Map<String, Object> responseError(Exception e) {
        e.printStackTrace();
        Map<String, Object> map = new HashMap<>();
        map.put("code", -1);
        map.put("msg", e.getMessage());
        return map;
    }

    @Resource
    private ApiAccountService apiAccountService;

    @Resource
    private ApiResourceService apiResourceService;
}
