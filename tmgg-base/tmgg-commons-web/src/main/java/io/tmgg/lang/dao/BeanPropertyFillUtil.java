package io.tmgg.lang.dao;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.lang.StrTool;
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;

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
     * @param obj
     */

    private static void fillPropertiesByAnn(Object obj) {
        Field[] declaredFields = obj.getClass().getDeclaredFields();

        for (Field f : declaredFields) {
            Annotation[] ans = f.getAnnotations();
            if (ans.length == 0) {
                continue;
            }

            // 必须是非持久化字段
            if (!f.isAnnotationPresent(Transient.class) &&
                !f.isAnnotationPresent(org.springframework.data.annotation.Transient.class)) {
                continue;
            }

            handleAutoFill(obj, f);


            if (f.isAnnotationPresent(AutoFillJoinField.class)) {
                AutoFillJoinField autoFill = f.getAnnotation(AutoFillJoinField.class);
                String label = new AutoFillJoinFieldHandler().getTargetValue(autoFill, obj);
                if (label != null) {
                    BeanUtil.setFieldValue(obj, f.getName(), label);
                }
            }
        }

    }

    private static void handleAutoFill(Object obj, Field f) {
        AutoFill autoFill = getAutoFill(f);
        if (autoFill == null) {
            return;
        }
        Class<? extends AutoFillStrategy> strategyClass = autoFill.value();
        try {
            AutoFillStrategy strategy = SpringUtil.getBean(strategyClass);

            // 获取原始字段
            // 规则1. 默认去掉最后一个单词， 例如 userLabel -> user
            String sourceField = removeLastWords(f.getName());

            if (!ReflectUtil.hasField(obj.getClass(), sourceField)) {
                sourceField = sourceField + "Id";
            }

            if (!ReflectUtil.hasField(obj.getClass(), sourceField)) {
                return;
            }

            Object sourceValue = BeanUtil.getFieldValue(obj, sourceField);
            if (sourceValue == null) {
                return;
            }

            Object value = strategy.getValue(obj, sourceValue, autoFill.param());
            BeanUtil.setFieldValue(obj, f.getName(), value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 去掉最后一个单词
     *
     * @param str
     * @return
     */
    private static String removeLastWords(String str) {
        int i = StrTool.lastUpperLetter(str);
        if (i == -1) {
            return str;
        }

        return StrUtil.sub(str, 0, i);
    }


    private static AutoFill getAutoFill(Field f) {
        AutoFill autoFill = f.getAnnotation(AutoFill.class);
        if (autoFill != null) {
            return autoFill;
        }

        Annotation[] as = f.getAnnotations();
        for (Annotation methodAnn : as) {
            AutoFill annAnn = methodAnn.annotationType().getAnnotation(AutoFill.class); // 注解的注解
            if (annAnn != null) {
                return annAnn;
            }
        }

        return null;
    }
}
