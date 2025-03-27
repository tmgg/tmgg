package io.tmgg.lang.dao;

import cn.hutool.core.util.IdUtil;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class IdGenerator implements IdentifierGenerator {

    public static final String CLASS_NAME = "io.tmgg.lang.dao.IdGenerator";

    private static Map<Object, String> ENTITY_ID_CACHE = new HashMap<>();

    public static void markId(Object entity, String id){
        ENTITY_ID_CACHE.put(entity,id);
    }



    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        Assert.state(CLASS_NAME.equals(getClass().getName()), "包名定义错误");


        this.params = params;
    }

    private Properties params;

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object entity) throws HibernateException {
        if(ENTITY_ID_CACHE.containsKey(entity)){
            return ENTITY_ID_CACHE.get(entity);
        }

        if (entity instanceof PersistEntity) {
            PersistEntity e = (PersistEntity) entity;
            if (e.getId() != null) {
                return e.getId();
            }

        }
        // 订单号算法
        if (entity instanceof BaseEntity e) {
            String customGenerateId = e.customGenerateId(params);
            if (customGenerateId != null) {
                return customGenerateId;
            }
        }

        return IdUtil.simpleUUID();
    }

}
