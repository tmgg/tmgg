package io.tmgg.modules.openapi.gateway;

import io.tmgg.jackson.JsonTool;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class ArgumentResolver {


    public static Object[] resolve(Method method, Map<String, Object> reqData, HttpServletRequest request, HttpServletResponse response) {
        Class<?>[] parameters = method.getParameterTypes();
        StandardReflectionParameterNameDiscoverer u = new StandardReflectionParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);


        Object[] args = new Object[parameters.length];// 参数长度
        for (int i = 0; i < parameters.length; i++) {
            Class<?> cls = parameters[i];
            if (cls == Integer.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Integer) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            }
            if (cls == int.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (int) reqData.get(paramNames[i]);
                } else {
                    args[i] = 0;
                }
                continue;
            }
            if (cls == Short.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Short) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            }
            if (cls == short.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (short) reqData.get(paramNames[i]);
                } else {
                    args[i] = 0;
                }
                continue;
            }
            if (cls == Float.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Float) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            }
            if (cls == float.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (float) reqData.get(paramNames[i]);
                } else {
                    args[i] = 0.0;
                }
                continue;
            }
            if (cls == Double.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Double) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            }
            if (cls == double.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (double) reqData.get(paramNames[i]);
                } else {
                    args[i] = 0.0;
                }
                continue;
            }
            if (cls == Long.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Long) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            }
            if (cls == long.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (long) reqData.get(paramNames[i]);
                } else {
                    args[i] = 0;
                }
                continue;
            }
            if (cls == Boolean.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Boolean) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            }
            if (cls == boolean.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (boolean) reqData.get(paramNames[i]);
                } else {
                    args[i] = false;
                }
                continue;
            }
            if (cls == Byte.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Byte) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            }
            if (cls == byte.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (byte) reqData.get(paramNames[i]);
                } else {
                    args[i] = 0;
                }
                continue;
            }
            if (cls == Character.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Character) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            }
            if (cls == char.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (char) reqData.get(paramNames[i]);
                } else {
                    args[i] = "";
                }
                continue;
            }
            if (cls == String.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (String) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            }
            if (cls == HttpServletRequest.class) {
                args[i] = request;
                continue;
            }
            if (cls == HttpServletResponse.class) {
                args[i] = response;
                continue;
            }
            if (cls == Map.class) {
                args[i] = reqData;
                continue;
            }
            if (cls == Set.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Set) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            }
            {
                try {
                    Object obj = cls.newInstance();
                    // BeanUtils.populate(obj, reqData);
                    args[i] = JsonTool.jsonToBean(JsonTool.toJsonQuietly(reqData), obj.getClass());
                } catch (Exception e) {
                    e.printStackTrace();
                    args[i] = null;
                }
            }
        }
        return args;
    }


}
