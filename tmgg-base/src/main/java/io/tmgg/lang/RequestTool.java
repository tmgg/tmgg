package io.tmgg.lang;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RequestTool {

    /**
     * 获取基础URL， 如http://127.0.0.1
     * 注意：末尾不含斜杠
     *
     * @param request 请求
     * @return  基础URL
     *
     */
    public static String getBaseUrl(HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        return baseUrl;
    }

    public static Map<String, String> getParamMap(HttpServletRequest request) {
        Enumeration<String> names = request.getParameterNames();

        Map<String, String> initData = new HashMap<>();
        while (names.hasMoreElements()) {
            String name = names.nextElement();

            if (!initData.containsKey(name)) {
                initData.put(name, request.getParameter(name));
            }
        }

        return initData;
    }

    public static HttpServletRequest currentRequest() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        if (sra != null) {
            return sra.getRequest();
        }
        return null;
    }


}
