package io.tmgg.web.persistence.converter;


import com.fasterxml.jackson.core.type.TypeReference;
import io.tmgg.jackson.JsonTool;
import jakarta.persistence.AttributeConverter;

import java.io.IOException;
import java.io.Serializable;

public class BaseConverter<T> implements AttributeConverter<T, String>, Serializable {


    private static final long serialVersionUID = 1L;

    @Override
    public String convertToDatabaseColumn(T input) {
        // hutool的数组有bug
        return JsonTool.toJsonQuietly(input);
    }


    @Override
    public T convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        // 兼容性处理
        if(dbData.equals("[]")){
            return null;
        }


        TypeReference<T> reference = new TypeReference<T>() {
        };
        try {
            return JsonTool.jsonToBean(dbData, reference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
