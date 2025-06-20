package io.tmgg.framework.dbconfig;

import cn.hutool.core.util.StrUtil;
import io.tmgg.event.SysConfigChangeEvent;
import io.tmgg.lang.SpringTool;
import io.tmgg.modules.system.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DbValueProcessor implements BeanPostProcessor  {

    private final Map<Object, Map<Field, DbValue>> beanRegistry = new ConcurrentHashMap<>();

    private boolean hasError = false;

    @NotNull
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        register(bean);

        Map<Field, DbValue> map = beanRegistry.get(bean);
        if (map != null) {
            map.forEach(((field, dbValue) -> injectValue(bean, field, dbValue)));
        }

        return bean;
    }

    private String[] basePackageNames;



    private void register(Object bean) {
        String name = bean.getClass().getName();

        // 缓存基础包，提升效率
        if(basePackageNames == null){
            basePackageNames = SpringTool.getBasePackageNames();
        }

        // 过滤包名，提升效率
        if(!StrUtil.startWithAny(name, basePackageNames)){
            return;
        }

        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(DbValue.class)) {
                DbValue annotation = field.getAnnotation(DbValue.class);
                Map<Field, DbValue> map = beanRegistry.computeIfAbsent(bean, k -> new HashMap<>());
                map.put(field, annotation);
                log.info("注册bean{}的DbValue注解{}", bean, annotation.value());
            }
        }
    }


    private void injectValue(Object bean, Field field, DbValue dbValue) {
        try {
            field.setAccessible(true);
            String key = dbValue.value();
            SysConfigService configService = SpringTool.getBean(SysConfigService.class);
            Object value = configService.getValueQuietly(key);
            if(value == null){
                return;
            }

            // 类型转换（例如 String -> Integer）
            Object convertedValue = SpringTool.getConfigurableBeanFactory()
                    .getConversionService()
                    .convert(value, field.getType());
            field.set(bean, convertedValue);
        } catch (IllegalAccessException e) {
            String message = "Failed to inject @DatabaseValue for field: " + field.getName();
            log.error(message);
            throw new IllegalStateException(message);
        }
    }


    @EventListener
    public void on(SysConfigChangeEvent e) {
        String key = e.getKey();

        beanRegistry.forEach((bean, fields) -> {
            fields.forEach((field, dbValue) -> {
                // 重新注入值
                String dbKey = dbValue.value();
                if (key.equals(dbKey)) {
                    injectValue(bean, field, dbValue);
                }
            });
        });
    }

}
