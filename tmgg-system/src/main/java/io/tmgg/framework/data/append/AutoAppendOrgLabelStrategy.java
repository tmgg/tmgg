package io.tmgg.framework.data.append;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import io.tmgg.dbtool.DbTool;
import io.tmgg.web.persistence.fill.ValueConvertStrategy;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AutoAppendOrgLabelStrategy implements ValueConvertStrategy {

    private static Cache<String, String> ORG_ID_NAME_CACHE = CacheUtil.newTimedCache(1000 * 60 * 5);

    @Resource
    private DbTool db;

    private void init() {
        List<Map<String, Object>> list = db.findAll("select id,name from sys_org");
        for (Map<String, Object> item : list) {
            String id = (String) item.get("id");
            String name = (String) item.get("name");
            if (name != null) {
                ORG_ID_NAME_CACHE.put(id, name);
            }
        }
    }


    @Override
    public Object convertValue(Object bean, Object sourceValue, String param) {
        String orgId = (String) sourceValue;
        if (orgId == null) {
            return null;
        }
        synchronized (this) {
            if (ORG_ID_NAME_CACHE.containsKey(orgId)) {
                return ORG_ID_NAME_CACHE.get(orgId);
            }
            init();
        }

        if (ORG_ID_NAME_CACHE.containsKey(orgId)) {
            return ORG_ID_NAME_CACHE.get(orgId);
        }

        return null;
    }
}
