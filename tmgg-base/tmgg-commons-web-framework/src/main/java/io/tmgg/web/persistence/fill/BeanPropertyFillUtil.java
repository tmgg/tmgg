package io.tmgg.web.persistence.fill;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.lang.StrTool;
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Slf4j
public class BeanPropertyFillUtil {


    public static final String SOURCE_FIELD_SUFFIX_ID = "Id";

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
            handleAutoFill2(bean, f);
            if (f.isAnnotationPresent(AutoAppendRelatedField.class)) {
                AutoAppendRelatedField ann = f.getAnnotation(AutoAppendRelatedField.class);
                String label = new AutoAppendRelatedFieldHandler().getTargetValue(f.getName(), ann, bean);
                if (label != null) {
                    BeanUtil.setFieldValue(bean, ann.appendField(), label);
                }
            }
        }

    }

    private static void handleAutoFill2(Object obj, Field f) {
        AutoAppendField autoFill = getAutoFill(f);
        if (autoFill == null) {
            return;
        }


        log.warn("AutoAppendField注解已经弃用，请尽快修改为@Fillxxx {} {}", obj.getClass().getSimpleName(), f.getName());
        String name = f.getName();
        Assert.state(!name.endsWith("Label"), "Auto注解已调整，请放到原始字段上");
        Assert.state(!f.isAnnotationPresent(Transient.class), "Auto注解已调整，请放到原始字段上");
        Assert.state(!f.isAnnotationPresent(org.springframework.data.annotation.Transient.class), "Auto注解已调整，请放到原始字段上");

        Class<? extends ValueConvertStrategy> strategyClass = autoFill.value();
        try {
            ValueConvertStrategy strategy = SpringUtil.getBean(strategyClass);

            // 获取原始字段
            // 规则1. 默认去掉最后一个单词， 例如 userLabel -> user
            String targetField = f.getName();
            if (autoFill.removeIdStr()) {
                targetField = StrUtil.removeSuffix(targetField, SOURCE_FIELD_SUFFIX_ID);
            }
            targetField += autoFill.suffix();


            if (!ReflectUtil.hasField(obj.getClass(), targetField)) {
                return;
            }

            Object sourceValue = BeanUtil.getFieldValue(obj, f.getName());
            if (sourceValue == null) {
                return;
            }

            Object targetValue = strategy.convertValue(obj, sourceValue, autoFill.param());
            BeanUtil.setFieldValue(obj, targetField, targetValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleAutoFill(Object obj, Field f) {
        String key = obj.getClass().getSimpleName() + "." + f.getName();

        FillField ff = AnnotatedElementUtils.findMergedAnnotation(f, FillField.class);
        if(ff == null){
            return;
        }
        Class<? extends ValueConvertStrategy> strategyClass = ff.strategy();
        if (strategyClass == null) {
            return;
        }
        Assert.state(f.isAnnotationPresent(org.springframework.data.annotation.Transient.class) || f.isAnnotationPresent(Transient.class), "注解请放到@Transient字段上 " + key);

        try {
            ValueConvertStrategy strategy = SpringUtil.getBean(strategyClass);

            String targetField = f.getName();
            String sourceField = StrTool.removeLastWord(targetField);


            if (!ReflectUtil.hasField(obj.getClass(), sourceField)) {
                // 尝试增加id后缀
                sourceField = sourceField + SOURCE_FIELD_SUFFIX_ID;
                if (!ReflectUtil.hasField(obj.getClass(), sourceField)) {
                    return;
                }
            }

            Object sourceValue = BeanUtil.getFieldValue(obj, sourceField);
            if (sourceValue == null) {
                return;
            }

            Object targetValue = strategy.convertValue(obj, sourceValue, ff.params());
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
