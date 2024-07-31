package io.tmgg.lang.dao;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class AutoFillJoinFieldHandler {


    private static final Cache<Object,String> LRU_CACHE = CacheUtil.newLRUCache(5000, 1000 * 60 * 5);

    public String getTargetValue(AutoFillJoinField field, Object obj) {
        Object sourceValue = BeanUtil.getFieldValue(obj, field.sourceField());
        if(sourceValue == null){
            return null;
        }

        if(LRU_CACHE.containsKey(sourceValue)){
            return LRU_CACHE.get(sourceValue);
        }

        String targetField = field.joinField();
        Class<? extends BaseEntity> targetEntity = field.joinEntity();

        String name = targetEntity.getSimpleName();

        Entity entityAnn = targetEntity.getAnnotation(Entity.class);
        if (StrUtil.isNotEmpty(entityAnn.name()) ) {
            name = entityAnn.name();
        }


        String jpql = "select " + field.returnField() + " from " + name + " where " + targetField + "=" + sourceValue;

        EntityManager em = SpringUtil.getBean(EntityManager.class);

        Query query = em.createQuery(jpql);

        try {
            Object result = query.getSingleResult();

            if(result != null){
                String resultStr = result.toString();

                LRU_CACHE.put(sourceValue, resultStr);

                return resultStr;
            }
        }catch (Exception e){
            return null;
        }



        return null;
    }

}
