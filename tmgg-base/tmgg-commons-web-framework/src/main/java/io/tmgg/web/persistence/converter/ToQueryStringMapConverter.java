package io.tmgg.web.persistence.converter;

import cn.hutool.core.util.URLUtil;
import io.tmgg.lang.URLTool;
import jakarta.persistence.AttributeConverter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 转换为 a=1&b=2这种格式
 */
public class ToQueryStringMapConverter implements AttributeConverter<Map<String,Object>, String>, Serializable {


    @Override
    public String convertToDatabaseColumn(Map<String, Object> map) {
        if(map == null || map.isEmpty()){
            return null;
        }
        String query = URLUtil.buildQuery(map, StandardCharsets.UTF_8);
        return query;
    }


    @Override
    public Map<String, Object> convertToEntityAttribute(String queryString) {
        Map<String, Object> map = URLTool.queryStringToMap(queryString);
        return map;
    }
}
