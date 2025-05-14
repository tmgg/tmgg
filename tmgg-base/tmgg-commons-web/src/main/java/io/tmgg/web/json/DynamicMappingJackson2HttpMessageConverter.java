package io.tmgg.web.json;

import cn.hutool.core.collection.IterUtil;
import com.fasterxml.jackson.databind.*;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.web.WebConstants;
import io.tmgg.web.json.ignore.JsonIgnoreIntrospector;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

public class DynamicMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private final ChangeMethodPublicJackson2HttpMessageConverter customConverter;

    public DynamicMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);

        ObjectMapper copy = objectMapper.copy();
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
     * 为了让 writeInternal 可以被调用，重写了该方法
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
