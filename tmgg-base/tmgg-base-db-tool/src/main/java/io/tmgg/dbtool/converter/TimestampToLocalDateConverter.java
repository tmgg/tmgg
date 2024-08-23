package io.tmgg.dbtool.converter;

import io.tmgg.dbtool.Converter;

import java.beans.PropertyDescriptor;
import java.sql.Timestamp;
import java.time.LocalDate;

public class TimestampToLocalDateConverter implements Converter {



    @Override
    public boolean match(Class<?> dbData, PropertyDescriptor target) {
        return dbData == Timestamp.class && target.getPropertyType() == LocalDate.class;
    }

    @Override
    public Object convertTo(Object dbData, Class<?> targetType) {
        return  ((Timestamp)dbData).toLocalDateTime().toLocalDate();
    }


}
