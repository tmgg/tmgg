package io.tmgg.dbtool.converter;

import io.tmgg.dbtool.Converter;
import io.tmgg.dbtool._Util;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;


/**
 * convert to List String
 */
public class StringToListIntConverter implements Converter {
    @Override
    public boolean match(Class<?> dbData, PropertyDescriptor target ) {
        boolean isList = List.class.isAssignableFrom(target.getPropertyType());
        return isList && dbData == String.class && _Util.getFirstGeneric(target) == Integer.class;
    }




    @Override
    public Object convertTo(Object dbData, Class<?> targetType) {
        List<Integer> list = new ArrayList<>();

        String str = (String) dbData;
        if (!str.isEmpty()) {
            String[] arr = str.split(",");

            for (String s : arr) {
                int i = Integer.parseInt(s);
                list.add(i);
            }

        }
        return list;
    }


}
