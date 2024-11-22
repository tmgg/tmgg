package io.tmgg.lang.dao;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

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


    /**
     *
     * @param obj
     */

    private static void fillPropertiesByAnn(Object obj) {
        Field[] declaredFields = obj.getClass().getDeclaredFields();

        for (Field f : declaredFields) {
            if (f.isAnnotationPresent(AutoFill.class)) {
                AutoFill autoFill = f.getAnnotation(AutoFill.class);

                Class<? extends AutoFillStrategy> strategyClass = autoFill.value();
                try {
                    AutoFillStrategy strategy = SpringUtil.getBean(strategyClass);


                    // 获取原始字段
                    // 规则1. 默认去掉Label， 例如 userLabel -> user
                    String sourceField1 = StrUtil.replace(f.getName(), "Label", "");


                    Object sourceValue = BeanUtil.getFieldValue(obj, sourceField1);
                    if(sourceValue == null){
                        // 规则2.  如 userLabel->userId
                        String sourceField2 = sourceField1 + "Id";
                        sourceValue =  BeanUtil.getFieldValue(obj, sourceField2);
                    }

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
