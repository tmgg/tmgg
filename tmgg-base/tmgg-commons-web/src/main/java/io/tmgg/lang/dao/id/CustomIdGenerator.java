package io.tmgg.lang.dao.id;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.dao.PersistEntity;
import io.tmgg.lang.dao.id.impl.DailyTableGenerator;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class CustomIdGenerator implements IdentifierGenerator {

    private final CustomId cfg;

    private int count = 0;


    private IdentifierGenerator generator;


    public CustomIdGenerator(CustomId config, Member annotatedMember,
                             CustomIdGeneratorCreationContext context) {
        CustomId override = getOverride(context);
        this.cfg = override != null ? override : config;

        switch (cfg.style()) {
            case DAILY_SEQ -> generator = new DailyTableGenerator(cfg.length() - cfg.prefix().length());
            case UUID -> generator = (session, object) -> IdUtil.simpleUUID();
            case DATETIME_UUID -> generator = (session, object) -> time() + IdUtil.simpleUUID();
            case DATETIME_SEQ -> generator = (session, object) -> time() + count;
        }
    }

    @Override
    public void initialize(SqlStringGenerationContext context) {
            generator.initialize(context);
    }

    @Override
    public void registerExportables(Database database) {
            generator.registerExportables(database);
    }

    @Override
    public void configure(Type type, Properties parameters, ServiceRegistry serviceRegistry) {
            generator.configure(type, parameters, serviceRegistry);
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object entity) {
        String id = getEntityId(entity);
        if (id != null) {
            return id;
        }

        count++;

        String prefix = cfg.prefix();
        Object nextId = generator.generate(session, entity);

        return joinId(prefix,  String.valueOf(nextId));
    }


    private static String time() {
        return DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN);
    }



    private String joinId(String prefix, String suffix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);

        int maxLen = cfg.length();
        int len = maxLen - sb.length();

        if (suffix.length() > len) {
            suffix = suffix.substring(0, len);
        } else if (suffix.length() < len) {
            suffix = StrUtil.padPre(suffix, len, "0");
        }

        sb.append(suffix);

        return sb.toString();

    }

    private static String getEntityId(Object entity) {
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

    /**
     * 查看注解是否被覆盖，顺序： 类型上的注解，字段上的注解
     *
     * @param context
     * @return
     */
    private static CustomId getOverride(CustomIdGeneratorCreationContext context) {
        Class<?> domainClass = context.getRootClass().getMappedClass();
        CustomId ann = domainClass.getAnnotation(CustomId.class);
        if (ann != null) {
            return ann;
        }


        String fieldName = context.getProperty().getName();
        Field[] fields = domainClass.getDeclaredFields();
        Field f = Arrays.stream(fields).filter(field -> field.getName().equals(fieldName)).findFirst().orElse(null);
        if (f == null) {
            return null;
        }
        return f.getAnnotation(CustomId.class);

    }


}
  