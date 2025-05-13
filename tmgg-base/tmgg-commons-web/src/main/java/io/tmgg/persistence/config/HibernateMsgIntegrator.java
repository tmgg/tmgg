package io.tmgg.persistence.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import io.tmgg.lang.ann.Msg;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 自定生成注释，根据注解
 */
@Slf4j
public class HibernateMsgIntegrator implements Integrator {
    public static final HibernateMsgIntegrator INSTANCE = new HibernateMsgIntegrator();

    public HibernateMsgIntegrator() {
        super();
    }

    /**
     * Perform comment integration.
     *
     * @param metadata        The "compiled" representation of the mapping information
     * @param sessionFactory  The session factory being created
     * @param serviceRegistry The session factory's service registry
     */
    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        processComment(metadata);
    }

    /**
     * Not used.
     *
     * @param sessionFactoryImplementor     The session factory being closed.
     * @param sessionFactoryServiceRegistry That session factory's service registry
     */
    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactoryImplementor, SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {
    }

    /**
     * Process comment annotation.
     *
     * @param metadata process annotation of this {@code Metadata}.
     */
    private void processComment(Metadata metadata) {
        for (PersistentClass persistentClass : metadata.getEntityBindings()) {
            // Process the Comment annotation is applied to Class
            Class<?> clz = persistentClass.getMappedClass();
            if (clz.isAnnotationPresent(Msg.class)) {
                Msg msg = clz.getAnnotation(Msg.class);
                persistentClass.getTable().setComment(msg.value());
            }

            // Process Comment annotations of identifier.
            Property identifierProperty = persistentClass.getIdentifierProperty();
            if (identifierProperty != null) {
                fieldComment(persistentClass, identifierProperty.getName());
            } else {
                org.hibernate.mapping.Component component = persistentClass.getIdentifierMapper();
                if (component != null) {
                    //noinspection unchecked
                    Iterator<Property> iterator = component.getPropertyIterator();
                    while (iterator.hasNext()) {
                        fieldComment(persistentClass, iterator.next().getName());
                    }
                }
            }
            // Process fields with Comment annotation.
            //noinspection unchecked
            List<Property> properties = persistentClass.getProperties();
            for (Property property : properties) {

                fieldComment(persistentClass, property.getName());
            }
        }
    }

    /**
     * Process @{code comment} annotation of field.
     *
     * @param persistentClass Hibernate {@code PersistentClass}
     * @param columnName      name of field
     */
    private void fieldComment(PersistentClass persistentClass, String columnName) {

        try {
            Class<?> cls = persistentClass.getMappedClass();
            Field field = ClassUtil.getDeclaredField(cls, columnName);
            if(field == null){
                return;
            }
            if (field.isAnnotationPresent(Msg.class)) {
                String comment = field.getAnnotation(Msg.class).value();

                log.debug("设置数据库表的注释 {}.{}：{}", persistentClass.getTable().getName(), columnName, comment);

                String sqlColumnName = persistentClass.getProperty(columnName).getValue().getColumns().iterator().next().getText();
                Collection<Column> columns = persistentClass.getTable().getColumns();
                if (CollUtil.isEmpty(columns)) {
                    return;
                }
                for (org.hibernate.mapping.Column column : columns) {
                    if (sqlColumnName.equalsIgnoreCase(column.getName())) {
                        column.setComment(comment);
                        break;
                    }
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }



}
