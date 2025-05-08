package io.tmgg.config;

import io.tmgg.lang.UserAgentTool;
import io.tmgg.lang.ann.Msg;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.poi.ExcelExportTool;
import io.tmgg.modules.sys.entity.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;
import java.util.ArrayList;

@ControllerAdvice
public class ExcelResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return AjaxResult.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, 
                                MediaType selectedContentType,
                                Class selectedConverterType,
                                ServerHttpRequest request,
                                ServerHttpResponse response) {

        HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
        HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();


        String accept = req.getHeader("X-Export-Excel");

        if ("true".equals(accept)) {
            AjaxResult r = (AjaxResult) body;
            Object data = r.getData();
            Page<?> page = (Page<?>) data;


            Class<?> cls = r.getEntityClass();
            Assert.notNull(cls,"cls不能为空，请调用"+ AjaxResult.class.getSimpleName() +"的cls方法设置导出实体类型");
            Msg msg = cls.getAnnotation(Msg.class);
            String filename = msg != null ? msg.value() : cls.getSimpleName();
            try {
                ExcelExportTool.exportBeanList(filename+".xlsx", page.getContent() , cls, resp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return null;
        }
        return body; // 否则返回原 JSON 数据
    }
}