package io.tmgg.lang.dao.id;

import cn.hutool.core.util.IdUtil;
import io.tmgg.lang.dao.PersistEntity;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;

import java.lang.reflect.Member;
import java.util.EnumSet;

public class CustomIdGenerator implements IdentifierGenerator {

    CustomId config;

    public CustomIdGenerator(CustomId config, Member annotatedMember,
                             CustomIdGeneratorCreationContext context) {

        this.config = config;
    }


    @Override
    public Object generate(SharedSessionContractImplementor session, Object entity) {
        if (entity instanceof PersistEntity e) {
            if (e.getId() != null) {
                return e.getId();
            }

            String id = EntityIdCacheTool.get(e);
            if(id != null){
                return id;
            }
        }

        String prefix = config.prefix();
        String id = IdUtil.simpleUUID();
        return prefix +id;
    }


}
  