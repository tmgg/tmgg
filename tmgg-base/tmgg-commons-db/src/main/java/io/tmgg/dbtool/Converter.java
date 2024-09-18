package io.tmgg.dbtool;


import java.beans.PropertyDescriptor;

public interface Converter {

    boolean match(Class<?> dbData, PropertyDescriptor targetType);


    Object convertTo(Object dbData, Class<?> targetType);
}
