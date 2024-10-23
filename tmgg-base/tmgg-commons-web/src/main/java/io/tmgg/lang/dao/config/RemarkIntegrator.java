package io.tmgg.lang.dao.config;

import io.tmgg.lang.ann.Remark;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

public class RemarkIntegrator implements Integrator {
    public static final RemarkIntegrator INSTANCE = new RemarkIntegrator();

    public RemarkIntegrator() {
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
            if (clz.isAnnotationPresent(Remark.class)) {
                Remark remark = clz.getAnnotation(Remark.class);
                persistentClass.getTable().setComment(remark.value());
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
     * @param columnName            name of field
     */
    private void fieldComment(PersistentClass persistentClass, String columnName) {
        try {
            Field field = persistentClass.getMappedClass().getDeclaredField(columnName);
            if (field.isAnnotationPresent(Remark.class)) {
                String comment = field.getAnnotation(Remark.class).value();
                String sqlColumnName= persistentClass.getProperty(columnName).getValue().getColumns().iterator().next().getText();
                for (org.hibernate.mapping.Column column : persistentClass.getTable().getColumns()) {
                    if (sqlColumnName.equalsIgnoreCase(column.getName())) {
                        column.setComment(comment);
                        break;
                    }
                }
            }
        } catch (NoSuchFieldException | SecurityException ignored) {
        }
    }
}
