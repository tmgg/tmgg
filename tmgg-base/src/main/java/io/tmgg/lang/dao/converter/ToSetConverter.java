package io.tmgg.lang.dao.converter;


import cn.hutool.core.util.StrUtil;

import javax.persistence.AttributeConverter;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 字符串，数组
 *
 * @author 姜涛
 * 使用方式：getter上加	@Convert(converter = StringArrayConverter.class)
 */
public class ToSetConverter implements AttributeConverter<Set<String>, String>, Serializable {


    private static final long serialVersionUID = 1L;
    public static final String CONJUNCTION = ",";

    @Override
    public String convertToDatabaseColumn(Set<String> list) {
        if (list == null) {
            return null;
        }

        return StrUtil.join(CONJUNCTION, list);
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.length() == 0) {
            return new HashSet<>();
        }
        return new HashSet<>(StrUtil.split(dbData, CONJUNCTION));
    }

}
