package io.tmgg.framework.dbconfig;

import io.tmgg.event.SysConfigChangeEvent;
import io.tmgg.event.SystemDataInitFinishEvent;
import io.tmgg.lang.SpringTool;
import io.tmgg.modules.sys.service.SysConfigService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DbValueProcessor {

    private final Map<Object, Map<Field, DbValue>> beanRegistry = new ConcurrentHashMap<>();

    private void register(Object bean) {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(DbValue.class)) {
                DbValue annotation = field.getAnnotation(DbValue.class);
                beanRegistry.computeIfAbsent(bean, k -> new ConcurrentHashMap<>()).put(field, annotation);
            }
        }
    }

    @EventListener
    public void on(SystemDataInitFinishEvent e){
        Collection<Object> beans = SpringTool.getBeans(Object.class);

        for (Object bean : beans) {
            register(bean);
        }

        beanRegistry.forEach((bean, fields) -> {
            fields.forEach((field, annotation) -> {
                // 重新注入值
                injectValue(bean, field, annotation);
            });
        });
    }

    @EventListener
    public void on(SysConfigChangeEvent e){
        String key = e.getKey();

        beanRegistry.forEach((bean, fields) -> {
            fields.forEach((field, dbValue) -> {
                // 重新注入值
                String dbKey = dbValue.value();
                if(key.equals(dbKey)){
                    injectValue(bean, field, dbValue);
                }
            });
        });
    }


    private void injectValue(Object bean, Field field, DbValue annotation) {
        try {
            field.setAccessible(true);
            String key = annotation.value();
            SysConfigService configService = SpringTool.getBean(SysConfigService.class);
            String value = configService.getStr(key);


            // 类型转换（例如 String -> Integer）
            Object convertedValue = SpringTool.getConfigurableBeanFactory()
                    .getConversionService()
                    .convert(value, field.getType());
            field.set(bean, convertedValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to inject @DatabaseValue for field: " + field.getName(), e);
        }
    }





}
