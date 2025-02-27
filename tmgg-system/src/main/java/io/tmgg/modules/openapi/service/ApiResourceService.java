package io.tmgg.modules.openapi.service;

import io.tmgg.lang.SpringTool;
import io.tmgg.modules.openapi.OpenApi;
import io.tmgg.modules.openapi.ApiResource;
import io.tmgg.modules.openapi.gateway.BaseOpenApi;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApiResourceService {

    private Map<String, ApiResource> map;

    public ApiResource findByMethod(String uri) {
        init();

        return map.get(uri);
    }

    public Collection<ApiResource> findAll(){
        init();
        return map.values();
    }

    /**
     * 延迟初始化，节约内存
     */
    private void init() {
        if (map != null) {
            return;
        }
        map = new HashMap<>();
        Collection<BaseOpenApi> beans = SpringTool.getBeans(BaseOpenApi.class);
        for (BaseOpenApi baseOpenApi : beans) {
            Method[] methods = baseOpenApi.getClass().getMethods();

            for (Method method : methods) {
                OpenApi openApi = method.getAnnotation(OpenApi.class);
                if (openApi != null) {
                    String url = openApi.action();
                    map.put(url, new ApiResource(baseOpenApi, method, openApi));
                }
            }
        }
    }


}
