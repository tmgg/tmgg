package io.tmgg.framework.data.append;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import io.tmgg.dbtool.DbTool;
import io.tmgg.modules.system.entity.SysOrg;
import io.tmgg.modules.system.service.SysOrgService;
import io.tmgg.web.persistence.fill.ValueConvertStrategy;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AutoAppendOrgLabelStrategy implements ValueConvertStrategy {


    Cache<String, String> cache = CacheUtil.newLRUCache(100, 1000 * 60 * 5);

    @Resource
    DbTool db;

    @PostConstruct
    public void init() {
        List<Map<String, Object>> list = db.findAll("select id,name from sys_org");
        for (Map<String, Object> item : list) {
            String id = (String) item.get("id");
            String name = (String) item.get("name");
            cache.put(id, name);
        }
    }


    @Override
    public Object convertValue(Object bean, Object sourceValue, String param) {
        String orgId = (String) sourceValue;


        if (orgId == null) {
            return null;
        }

        if (cache.containsKey(orgId)) {
            return cache.get(orgId);
        }

        init();
        if (cache.containsKey(orgId)) {
            return cache.get(orgId);
        }

        return null;
    }
}
