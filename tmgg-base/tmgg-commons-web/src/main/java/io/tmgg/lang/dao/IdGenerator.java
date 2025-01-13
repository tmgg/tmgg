package io.tmgg.lang.dao;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.springframework.data.domain.Persistable;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Properties;

public class IdGenerator implements IdentifierGenerator {

    public static final String CLASS_NAME = "io.tmgg.lang.dao.IdGenerator";


    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        Assert.state(CLASS_NAME.equals(getClass().getName()), "包名定义错误");


        this.params = params;
    }

    private Properties params;

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        if (o instanceof Persistable) {
            Persistable<String> e = (Persistable<String>) o;
            if (e.getId() != null) {
                return e.getId();
            }
        }

        // 订单号算法
        if (o instanceof BaseEntity) {
            String customGenerateId = ((BaseEntity) o).customGenerateId(params);
            if (customGenerateId != null) {
                return customGenerateId;
            }
        }

        return IdUtil.simpleUUID();
    }

}
