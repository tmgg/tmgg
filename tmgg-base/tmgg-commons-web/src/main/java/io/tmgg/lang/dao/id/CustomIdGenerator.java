package io.tmgg.lang.dao.id;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.dao.PersistEntity;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.mapping.RootClass;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.lang.reflect.Member;
import java.util.Properties;

public class CustomIdGenerator implements IdentifierGenerator {


    final String prefix;

    public CustomIdGenerator(CustomId config, Member annotatedMember,
                             CustomIdGeneratorCreationContext context) {
        String rootClass = context.getRootClass().getClassName();
        this.prefix = StrUtil.emptyToDefault(config.prefix(), null);
    }



    @Override
    public Object generate(SharedSessionContractImplementor session, Object entity) {
        String id = getEntityId(entity);
        if (id != null) {
            return id;
        }

        id = IdUtil.simpleUUID();
        if (prefix != null) {
            id = prefix + id;
        }

        return id;
    }

    private String getEntityId(Object entity) {
        if (entity instanceof PersistEntity e) {
            if (e.getId() != null) {
                return e.getId();
            }

            String id = EntityIdHolder.get(e);
            if (id != null) {
                return id;
            }
        }
        return null;
    }


}
  