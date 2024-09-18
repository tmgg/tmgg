package io.tmgg.dbtool.converter;

import io.tmgg.dbtool.Converter;
import io.tmgg.dbtool._Util;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * convert to List String
 */
public class StringToListConverter implements Converter {
    @Override
    public boolean match(Class<?> dbData, PropertyDescriptor target) {
        boolean isList = List.class.isAssignableFrom(target.getPropertyType());
        return isList && dbData == String.class && _Util.getFirstGeneric(target) == String.class;
    }

    @Override
    public Object convertTo(Object dbData, Class<?> targetType) {
        List<String> list = new ArrayList<>();

        String str = (String) dbData;
        if (!str.isEmpty()) {
            String[] arr = str.split(",");
            Collections.addAll(list, arr);

        }
        return list;
    }


}
