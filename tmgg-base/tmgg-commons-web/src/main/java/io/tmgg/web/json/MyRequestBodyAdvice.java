package io.tmgg.web.json;

import io.tmgg.web.persistence.BaseEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

@RestControllerAdvice
public class MyRequestBodyAdvice extends RequestBodyAdviceAdapter {
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?>[] parameterTypes = methodParameter.getMethod().getParameterTypes();
        return ArrayUtils.contains(parameterTypes, RequestBodyKeys.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        byte[] content = StreamUtils.copyToByteArray(inputMessage.getBody());

        return new MappingJacksonInputMessage(new ByteArrayInputStream(content),inputMessage.getHeaders());
    }
}
