package io.tmgg.modules.api.gateway;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
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
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/gateway")
public class ApiGatewayController {


    @PostMapping
    public Object process(HttpServletRequest request, HttpServletResponse response,
                          @RequestHeader("X-URI") String uri,
                          @RequestHeader("X-APP-KEY") String appKey,
                          @RequestHeader("X-TIMESTAMP") long timestamp,
                          @RequestBody String body) throws Exception {
        // 验证时间戳，与服务器时间差异不能超过20分钟
        long diffTime = (System.currentTimeMillis() - timestamp) / (1000 * 60); // 分钟
        Assert.state(Math.abs(diffTime) < 2, "时间戳差异大，差距" + diffTime + "分钟");

        // 校验签名
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

        Method method = resource.getMethod();

        Map<String, Object> params = JsonTool.jsonToMap(body);

        Object[] paramValues = ArgumentResolver.resolve(method, params , request, response);
        Object retValue = null;
        if (paramValues.length == 0) {
            retValue = method.invoke(resource.getBean());
        } else {
            Map<String, Object> data = new HashMap<>();
            retValue = method.invoke(resource.getBean(), paramValues);
        }

        return retValue;
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
