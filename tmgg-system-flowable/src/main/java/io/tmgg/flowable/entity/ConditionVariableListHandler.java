package io.tmgg.flowable.entity;


import com.fasterxml.jackson.core.type.TypeReference;
import io.tmgg.dbtool.Converter;
import io.tmgg.lang.JsonTool;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.List;

public class ConditionVariableListHandler implements Converter {


    @Override
    public boolean match(Class<?> cls, PropertyDescriptor p) {
        return p.getName().equalsIgnoreCase("conditionVariableList");
    }


    @Override
    public Object convertTo(Object dbData, Class<?> targetType) {
        if (dbData != null) {
            return convertToEntityAttribute((String) dbData);
        }
        return null;
    }


    public List<ConditionVariable> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        TypeReference<List<ConditionVariable>> reference = new TypeReference<List<ConditionVariable>>() {
        };
        try {
            return JsonTool.jsonToBean(dbData, reference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
