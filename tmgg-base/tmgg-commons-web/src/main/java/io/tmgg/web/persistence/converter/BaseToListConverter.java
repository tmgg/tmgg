package io.tmgg.web.persistence.converter;


import io.tmgg.jackson.JsonTool;
import io.tmgg.web.persistence.ReflectTool;
import jakarta.persistence.AttributeConverter;

import java.io.Serializable;
import java.util.List;

public  class BaseToListConverter<T> implements AttributeConverter<List<T>, String>, Serializable {


    private static final long serialVersionUID = 1L;


    @Override
    public String convertToDatabaseColumn(List<T> ts) {
        return JsonTool.toJsonQuietly(ts);
    }

    @Override
    public List<T> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.length() == 0) {
            return null;
        }

        Class<T> cls = ReflectTool.getClassGenricType(getClass());

        return JsonTool.jsonToBeanListQuietly(dbData, cls);
    }



}
