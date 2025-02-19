package io.tmgg.modules.api.service;

import io.tmgg.lang.SpringTool;
import io.tmgg.modules.api.Api;
import io.tmgg.modules.api.ApiResource;
import io.tmgg.modules.api.gateway.BaseApi;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApiResourceService {


    private Map<String, ApiResource> map;

    private void init() {
        if (map != null) {
            return;
        }
        map = new HashMap<>();
        Collection<BaseApi> beans = SpringTool.getBeans(BaseApi.class);
        for (BaseApi baseApi : beans) {
            Method[] methods = baseApi.getClass().getMethods();

            for (Method method : methods) {
                Api api = method.getAnnotation(Api.class);
                if (api != null) {
                    String url = api.url();
                    map.put(url, new ApiResource(baseApi, method, api));
                }
            }
        }
    }

    public ApiResource findByMethod(String uri) {
        init();

        return map.get(uri);
    }
}
