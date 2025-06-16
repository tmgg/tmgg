package io.tmgg.web.persistence;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Slf4j
public class BeanPropertyFillUtil {


    /**
     * 填充一些字段，如创建人的姓名
     *
     * @param obj 对象
     */
    public static void fillBeanProperties(Object obj) {
        fillPropertiesByAnn(obj);
    }


    /**
     * @param bean
     */

    private static void fillPropertiesByAnn(Object bean) {
        Field[] declaredFields = bean.getClass().getDeclaredFields();

        for (Field f : declaredFields) {
            Annotation[] ans = f.getAnnotations();
            if (ans.length == 0) {
                continue;
            }
            handleAutoFill(bean, f);

            if (f.isAnnotationPresent(AutoAppendRelatedField.class)) {
                AutoAppendRelatedField ann = f.getAnnotation(AutoAppendRelatedField.class);
                String label = new AutoAppendRelatedFieldHandler().getTargetValue(f.getName(), ann, bean);
                if (label != null) {
                    BeanUtil.setFieldValue(bean, ann.appendField(), label);
                }
            }
        }

    }

    private static void handleAutoFill(Object obj, Field f) {
        AutoAppendField autoFill = getAutoFill(f);
        if (autoFill == null) {
            return;
        }

        log.debug("自动扩展字段 {} {}", obj.getClass().getSimpleName(), f.getName());
        String name = f.getName();
        Assert.state(!name.endsWith("Label"), "Auto注解已调整，请放到原始字段上");
        Assert.state(!f.isAnnotationPresent(Transient.class), "Auto注解已调整，请放到原始字段上");
        Assert.state(!f.isAnnotationPresent(org.springframework.data.annotation.Transient.class), "Auto注解已调整，请放到原始字段上");

        Class<? extends AutoAppendStrategy> strategyClass = autoFill.value();
        try {
            AutoAppendStrategy strategy = SpringUtil.getBean(strategyClass);

            // 获取原始字段
            // 规则1. 默认去掉最后一个单词， 例如 userLabel -> user
            String targetField = f.getName();
            if(autoFill.removeIdStr()){
                targetField = StrUtil.removeSuffix(targetField,"Id");
            }
            targetField += autoFill.suffix();



            if (!ReflectUtil.hasField(obj.getClass(), targetField)) {
                return;
            }

            Object sourceValue = BeanUtil.getFieldValue(obj, f.getName());
            if (sourceValue == null) {
                return;
            }

            Object targetValue = strategy.getAppendValue(obj, sourceValue, autoFill.param());
            BeanUtil.setFieldValue(obj, targetField, targetValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private static AutoAppendField getAutoFill(Field f) {
        AutoAppendField autoFill = f.getAnnotation(AutoAppendField.class);
        if (autoFill != null) {
            return autoFill;
        }

        Annotation[] as = f.getAnnotations();
        for (Annotation methodAnn : as) {
            AutoAppendField annAnn = methodAnn.annotationType().getAnnotation(AutoAppendField.class); // 注解的注解
            if (annAnn != null) {
                return annAnn;
            }
        }

        return null;
    }
}
