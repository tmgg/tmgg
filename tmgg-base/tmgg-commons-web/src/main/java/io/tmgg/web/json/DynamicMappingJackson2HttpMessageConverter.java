package io.tmgg.web.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.web.WebConstants;
import io.tmgg.web.json.ignore.JsonIgnoreIntrospector;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 让json解析支持自定义的注解
 *
 * 如 JsonIgnoreForApp
 *
 */
public class DynamicMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private final ChangeMethodPublicJackson2HttpMessageConverter customConverter;

    public DynamicMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);

        ObjectMapper copy = objectMapper.copy();
        // 自定义的JsonIgnoreForApp
        copy.setAnnotationIntrospector(new JsonIgnoreIntrospector());
        this.customConverter = new ChangeMethodPublicJackson2HttpMessageConverter(copy);
    }





    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if(isAppApi()){
           customConverter.writeInternal(object, type, outputMessage);
           return;
        }

        super.writeInternal(object, type, outputMessage);
    }




    private boolean isAppApi(){
        HttpServletRequest request = HttpServletTool.getRequest();
        return request != null && request.getRequestURI().startsWith(WebConstants.APP_API);
    }

    /**
     * 为了让 writeInternal 可以被调用，重写了该方法, 本身没有说明逻辑
     */
    private static class ChangeMethodPublicJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        public ChangeMethodPublicJackson2HttpMessageConverter(ObjectMapper objectMapper) {
            super(objectMapper);
        }

        public void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
            super.writeInternal(object, type, outputMessage);
        }
    }
}
