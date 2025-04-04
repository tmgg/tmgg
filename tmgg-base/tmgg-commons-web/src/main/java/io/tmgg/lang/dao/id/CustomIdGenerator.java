package io.tmgg.lang.dao.id;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.dao.PersistEntity;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;

import java.lang.reflect.Member;
import java.util.Date;

public class CustomIdGenerator implements IdentifierGenerator {


    private final String prefix;
    private   final IdStyle style;

    private final int maxLen;
    private final String rootClass;

    public CustomIdGenerator(CustomId config, Member annotatedMember,
                             CustomIdGeneratorCreationContext context) {
        this.rootClass = context.getRootClass().getClassName();
        this.prefix = StrUtil.emptyToDefault(config.prefix(), null);
        this.style = config.style();
        this.maxLen = config.length();
    }



    @Override
    public Object generate(SharedSessionContractImplementor session, Object entity) {
        String id = getEntityId(entity);
        if (id != null) {
            return id;
        }



        switch (style) {
            case UUID -> {
                return IdUtil.simpleUUID();
            }
            case PREFIX_DATETIME_UUID -> {
                StringBuilder sb = new StringBuilder();

                if (prefix != null) {
                    sb.append(prefix);
                    sb.append(DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN));
                    sb.append(IdUtil.simpleUUID());
                    if(sb.length() > maxLen){
                        sb.delete(maxLen, sb.length());
                    }
                }
                return sb.toString();
            }
            case PREFIX_DATE_SEQ -> {
                // todo
            }
        }


        return null;
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
  