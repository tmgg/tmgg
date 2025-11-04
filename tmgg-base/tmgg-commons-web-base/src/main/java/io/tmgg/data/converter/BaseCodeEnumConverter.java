package io.tmgg.data.converter;

import io.tmgg.web.enums.CodeEnum;
import jakarta.persistence.AttributeConverter;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * 只需简单实现一个类，即可转换
 * public class XConverter extends BaseEnumCodeConverter<UserType> {
 * }
 * @param <E>
 */
public abstract class BaseCodeEnumConverter<E extends Enum<E> & CodeEnum> implements AttributeConverter<E, Integer>, Serializable {

    private final Class<E> clazz;

    public BaseCodeEnumConverter() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;

        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        Type typeArgument = typeArguments[0];
        this.clazz = (Class<E>) typeArgument;
    }

    @Override
    public Integer convertToDatabaseColumn(E attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public E convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }

        for (E e : clazz.getEnumConstants()) {
            if (e.getCode() == dbData) {
                return e;
            }
        }

        throw new IllegalArgumentException("未知的枚举代码: " + dbData + " for " + clazz.getSimpleName());
    }
}
