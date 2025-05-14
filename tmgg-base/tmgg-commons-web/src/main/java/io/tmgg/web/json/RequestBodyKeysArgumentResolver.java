package io.tmgg.web.json;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RequestBodyKeysArgumentResolver implements HandlerMethodArgumentResolver {

    // 判断是否支持该参数类型
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(RequestBodyKeys.class);
    }

    // 解析参数
    @Override
    public Object resolveArgument(MethodParameter parameter, 
                                 ModelAndViewContainer mavContainer,
                                 NativeWebRequest webRequest, 
                                 WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();



        // 构造并返回自定义对象
        return new RequestBodyKeys();
    }
}