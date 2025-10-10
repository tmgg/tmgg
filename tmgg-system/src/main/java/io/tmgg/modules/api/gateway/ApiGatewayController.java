package io.tmgg.modules.api.gateway;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import io.tmgg.lang.SpringTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.api.ApiErrorCode;
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

@Slf4j
@RestController
@RequestMapping("api/gateway")
public class ApiGatewayController {


    public static final int TIME_DIFF_LIMIT = 5;


    @Resource
    private ApiAccountResourceService accountResourceService;


    @Resource
    private ApiAccessLogService accessLogService;


    @PostMapping("{action}")
    public AjaxResult process(
            @PathVariable String action,
            @RequestHeader String appId,
            @RequestHeader long timestamp,
            @RequestHeader String sign,
            @RequestParam Map<String, Object> params,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        long startTime = System.currentTimeMillis();

        // 验证时间戳，与服务器时间差异不能超过x分钟
        long diffTime = (System.currentTimeMillis() - timestamp) / 1000;
        this.check(Math.abs(diffTime) < TIME_DIFF_LIMIT, ApiErrorCode.TIME_BIG_DIF);


        ApiAccount account = apiAccountService.findByAppId(appId);
        this.check(account != null, ApiErrorCode.ACC_NOT_FOUND);
        this.check(account.getEnable(), ApiErrorCode.ACC_NOT_FORBIDDEN);



        // 校验是否超期
        if (account.getEndTime() != null) {
            this.check(DateUtil.current() < account.getEndTime().getTime(), ApiErrorCode.ACC_EXPIRE);
        }

        Method method = apiResourceService.findMethodByAction(action);
        this.check(method != null, ApiErrorCode.RES_NOT_FOUND);


        // 校验签名
        String appSecret = account.getAppSecret();
        String calcSign = ApiSignTool.sign(appId, appSecret, timestamp);
        this.check(sign.equals(calcSign),  ApiErrorCode.SIGN_ERROR);


        // 校验权限
        ApiAccountResource ar = accountResourceService.findByAccountAndAction(account, action);
        this.check(ar != null, ApiErrorCode.PERM_NOT_FOUND);
        this.check(ar.getEnable(), ApiErrorCode.PERM_DISABLE);


        String clientIP = JakartaServletUtil.getClientIP(request);

        this.check(StrUtil.isEmpty(account.getAccessIp()) || account.getAccessIp().contains(clientIP), ApiErrorCode.ACC_IP);


        Object retValue = dispatch(params, method, request, response);


        // 保存日志
        String ip = JakartaServletUtil.getClientIP(request);
        long time = System.currentTimeMillis() - startTime;
        accessLogService.add(timestamp, account, ar.getResource(), params, retValue, ip, time);

        return AjaxResult.ok().data(retValue);
    }

    private Object dispatch(Map<String, Object> params, Method method, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
        Object[] paramValues = ArgumentResolver.resolve(method, params, request, response);

        Class<?> declaringClass = method.getDeclaringClass();
        Object bean = SpringTool.getBean(declaringClass);

        Object retValue = method.invoke(bean, paramValues);

        return retValue;
    }

    private void check(Boolean state, ApiErrorCode errorCode) {
        if (!state) {
            throw new CodeException(errorCode.getCode(), errorCode.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult parseException(Throwable e) {
        e.printStackTrace();

        if (e instanceof InvocationTargetException ite) {
            e = ite.getTargetException();
        }


        int code = ApiErrorCode.GLOBAL_ERROR.getCode();
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
