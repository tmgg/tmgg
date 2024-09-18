package io.tmgg.lang.dao;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class BeanPropertyFillUtil {


    /**
     * 填充一些字段，如创建人的姓名
     * @param obj  对象
     */
    public static void fillBeanProperties(Object obj) {
        fillPropertiesByAnn(obj);
    }

    private static void fillPropertiesByAnn(Object obj) {
        Field[] declaredFields = obj.getClass().getDeclaredFields();

        for (Field f : declaredFields) {
            if (f.isAnnotationPresent(AutoFill.class)) {
                AutoFill autoFill = f.getAnnotation(AutoFill.class);

                Class<? extends AutoFillStrategy> strategyClass = autoFill.value();
                try {
                    AutoFillStrategy strategy = SpringUtil.getBean(strategyClass);


                    String sourceField = autoFill.sourceField();

                    // 默认去掉Label， 例如 confirmedUserLabel, 去掉后变为 confirmedUser
                    if (sourceField == null || sourceField.isEmpty()) {
                        sourceField = StrUtil.replace(sourceField, "Label", "");
                    }

                    Object sourceValue = BeanUtil.getFieldValue(obj, sourceField);

                    Object value = strategy.getValue(obj, sourceValue, autoFill.param());

                    BeanUtil.setFieldValue(obj, f.getName(), value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (f.isAnnotationPresent(AutoFillJoinField.class)) {
                AutoFillJoinField autoFill = f.getAnnotation(AutoFillJoinField.class);
                String label = new AutoFillJoinFieldHandler().getTargetValue(autoFill, obj);
                if (label != null) {
                    BeanUtil.setFieldValue(obj, f.getName(), label);
                }
            }
        }

    }

}
