package io.tmgg.framework.data.append;

import io.tmgg.persistence.AutoAppendStrategy;
import io.tmgg.modules.sys.entity.SysOrg;
import io.tmgg.modules.sys.service.SysOrgService;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class AutoAppendOrgLabelStrategy implements AutoAppendStrategy {


    @Resource
    SysOrgService service;

    Cache<String,String> cache = CacheUtil.newLRUCache(100, 1000 * 60 * 5);


    @Override
    public Object getAppendValue(Object bean, Object sourceValue, String param) {
        String orgId = (String) sourceValue;


        if(orgId == null){
            return null;
        }

        if(cache.containsKey(orgId)){
            return cache.get(orgId);
        }

        SysOrg org = service.findOne(orgId);

        if(org == null){
            return null;
        }

        cache.put(orgId, org.getName());
        return org.getName();
    }
}
