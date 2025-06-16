package io.tmgg.web.persistence;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;


public class AutoAppendRelatedFieldHandler {


    private static final Cache<Object,String> LRU_CACHE = CacheUtil.newLRUCache(5000, 1000 * 60 * 5);

    public String getTargetValue(String sourceField, AutoAppendRelatedField field, Object obj) {
        Object sourceValue = BeanUtil.getFieldValue(obj, sourceField);
        if(sourceValue == null){
            return null;
        }

        String cacheKey = field.relatedEntity().getSimpleName() + "-" + sourceValue;

        if(LRU_CACHE.containsKey(cacheKey)){
            return LRU_CACHE.get(cacheKey);
        }

        String targetField = field.relatedField();
        Class<? extends BaseEntity> targetEntity = field.relatedEntity();

        String name = targetEntity.getSimpleName();

        Entity entityAnn = targetEntity.getAnnotation(Entity.class);
        if (StrUtil.isNotEmpty(entityAnn.name()) ) {
            name = entityAnn.name();
        }


        String jpql = "select " + field.relatedTargetField() + " from " + name + " where " + targetField + "=" + sourceValue;

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
