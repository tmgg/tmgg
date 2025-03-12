package io.tmgg.modules.openapi;

import io.tmgg.lang.RequestTool;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;

public class OpenApiTool {

    /**
     * 获取当前请求的appId
     * @return
     */
    public String currentAppId(){
        HttpServletRequest req = RequestTool.currentRequest();
        Assert.state(req != null, "获取当前请求失败");
        String appId = req.getHeader("x-app-id");
        return appId;
    }
}
