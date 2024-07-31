package io.tmgg.lang.dao.converter;


import cn.hutool.core.util.StrUtil;

import javax.persistence.AttributeConverter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 字符串，数组
 *
 * @author 姜涛
 * 使用方式：getter上加	@Convert(converter = StringArrayConverter.class)
 */
public class ToListConverter implements AttributeConverter<List<String>, String>, Serializable {


    private static final long serialVersionUID = 1L;
    public static final String CONJUNCTION = ",";

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        if (list == null) {
            return null;
        }

        return StrUtil.join(CONJUNCTION, list);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.length() == 0) {
            return new ArrayList<>();
        }
        return StrUtil.split(dbData, CONJUNCTION);
    }

}
