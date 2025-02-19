package io.tmgg.modules.api.gateway;

import io.tmgg.jackson.JsonTool;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class ArgumentResolver {

    public static Object[] resolve(Method method, Map<String, Object> reqData,HttpServletRequest request, HttpServletResponse response) {
        Class<?>[] parameters = method.getParameterTypes();
        StandardReflectionParameterNameDiscoverer u = new StandardReflectionParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);


        Object[] args = new Object[parameters.length];// 参数长度
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == Integer.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Integer) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            } else if (parameters[i] == int.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (int) reqData.get(paramNames[i]);
                } else {
                    args[i] = 0;
                }
                continue;
            } else if (parameters[i] == Short.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Short) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            } else if (parameters[i] == short.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (short) reqData.get(paramNames[i]);
                } else {
                    args[i] = 0;
                }
                continue;
            } else if (parameters[i] == Float.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Float) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            } else if (parameters[i] == float.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (float) reqData.get(paramNames[i]);
                } else {
                    args[i] = 0.0;
                }
                continue;
            } else if (parameters[i] == Double.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Double) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            } else if (parameters[i] == double.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (double) reqData.get(paramNames[i]);
                } else {
                    args[i] = 0.0;
                }
                continue;
            } else if (parameters[i] == Long.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Long) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            } else if (parameters[i] == long.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (long) reqData.get(paramNames[i]);
                } else {
                    args[i] = 0;
                }
                continue;
            } else if (parameters[i] == Boolean.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Boolean) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            } else if (parameters[i] == boolean.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (boolean) reqData.get(paramNames[i]);
                } else {
                    args[i] = false;
                }
                continue;
            } else if (parameters[i] == Byte.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Byte) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            } else if (parameters[i] == byte.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (byte) reqData.get(paramNames[i]);
                } else {
                    args[i] = 0;
                }
                continue;
            } else if (parameters[i] == Character.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Character) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            } else if (parameters[i] == char.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (char) reqData.get(paramNames[i]);
                } else {
                    args[i] = "";
                }
                continue;
            } else if (parameters[i] == String.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (String) reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            } else if (parameters[i] == HttpServletRequest.class) {
                args[i] = request;
                continue;
            } else if (parameters[i] == HttpServletResponse.class) {
                args[i] = response;
                continue;
            } else if (parameters[i] == Map.class) {
                args[i] = reqData;
                continue;
            } else if (parameters[i] == Set.class) {
                if (reqData.containsKey(paramNames[i])) {
                    args[i] = (Set)reqData.get(paramNames[i]);
                } else {
                    args[i] = null;
                }
                continue;
            } else {
                try {
                    Object obj = parameters[i].newInstance();
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
