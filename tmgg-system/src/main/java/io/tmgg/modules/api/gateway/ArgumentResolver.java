package io.tmgg.modules.api.gateway;

import cn.hutool.core.convert.Convert;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Map;

public class ArgumentResolver {


    public static Object[] resolve(Method method, Map<String, Object> reqData, HttpServletRequest request, HttpServletResponse response) {
        Class<?>[] parameters = method.getParameterTypes();
        StandardReflectionParameterNameDiscoverer u = new StandardReflectionParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);


        Object[] args = new Object[parameters.length];// 参数长度
        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramCls = parameters[i];
            String paramName = paramNames[i];
            Object paramValue = reqData.get(paramName);

            Object convertedValue = convert(paramCls, paramValue, request, response);
            args[i] = convertedValue;
        }
        return args;
    }


    private static Object convert(Class<?> cls, Object value, HttpServletRequest request, HttpServletResponse response) {
        if (value == null) {
            return null;
        }
        if (cls == value.getClass()) {
            return value;
        }
        if (cls == request.getClass()) {
            return request;
        }
        if (cls == response.getClass()) {
            return response;
        }

        return Convert.convert(cls, value);
    }

}
