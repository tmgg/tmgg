package io.tmgg.dbtool.converter;

import io.tmgg.dbtool.Converter;

import java.beans.PropertyDescriptor;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TimestampToLocalDateTimeConverter implements Converter {



    @Override
    public boolean match(Class<?> dbData, PropertyDescriptor target) {
        return dbData == Timestamp.class && target.getPropertyType() == LocalDateTime.class;
    }

    @Override
    public Object convertTo(Object dbData, Class<?> targetType) {
        return ((Timestamp ) dbData).toLocalDateTime();
    }

}
