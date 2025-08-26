package io.tmgg.lang;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class BeanReflectionTool {

    /**
     * 获取首个泛型类型
     * @param propertyDescriptor
     * @return
     */
    public static Type getFirstGeneric(PropertyDescriptor propertyDescriptor){
        Method readMethod = propertyDescriptor.getReadMethod();
        Type genericReturnType = readMethod.getGenericReturnType();

        if(genericReturnType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericReturnType;

            Type argument = pt.getActualTypeArguments()[0];

            return argument;
        }

        return null;
    }
}
