package io.tmgg.modules.dev;

import cn.hutool.extra.servlet.JakartaServletUtil;
import io.tmgg.jackson.JsonTool;
import io.tmgg.lang.RequestTool;
import io.tmgg.lang.obj.AjaxResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("requestTest")
public class RequestTestController {

    @GetMapping("get")
    public AjaxResult get(HttpServletRequest request) {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("请求时间", System.currentTimeMillis());
        data.put("基础URL", RequestTool.getBaseUrl(request));

        data.put("getRequestURI", request.getRequestURI());
        data.put("getAttributeNames", JsonTool.toJsonQuietly(request.getAttributeNames()));
        data.put("getCharacterEncoding", request.getCharacterEncoding());
        data.put("getContentLength", request.getContentLength());
        data.put("getContentLengthLong", request.getContentLengthLong());

        data.put("getContentType", request.getContentType());
        data.put("getParameterNames", JsonTool.toJsonQuietly(request.getParameterNames()));

        data.put("getParameterMap", JsonTool.toPrettyJsonQuietly(request.getParameterMap()));
        data.put("getProtocol", request.getProtocol());
        data.put("getScheme", request.getScheme());

        data.put("getServerName", request.getServerName());
        data.put("getServerPort", request.getServerPort());
        data.put("getRemoteAddr", request.getRemoteAddr());

        data.put("getRemoteHost", request.getRemoteHost());

        data.put("getLocale", request.getLocale().toString());

        data.put("getLocales", JsonTool.toPrettyJsonQuietly(request.getLocales()));
        data.put("isSecure", request.isSecure());

        data.put("getRemotePort", request.getRemotePort());

        data.put("getLocalName", request.getLocalName());

        data.put("getLocalAddr", request.getLocalAddr());
        data.put("getLocalPort", request.getLocalPort());
        data.put("request.getServletContext().getContextPath()", request.getServletContext().getContextPath());
        data.put("getDispatcherType", request.getDispatcherType().name());
        data.put("getRequestId", request.getRequestId());
        data.put("getProtocolRequestId", request.getProtocolRequestId());

        Map<String, String> headerMap = JakartaServletUtil.getHeaderMap(request);
        for (Map.Entry<String, String> e : headerMap.entrySet()) {
            String value = e.getValue();
            data.put("请求头 " + e.getKey(), value);
        }


        return AjaxResult.ok().data(data);
    }
}
