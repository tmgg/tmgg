package io.tmgg.dbtool;

import cn.hutool.core.util.StrUtil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class _Util {

    public static List<Map<String, Object>> camel(List<Map<String,Object>> list) {
        List<Map<String,Object>> newList = new ArrayList<>(list.size());
        for (Map<String, Object> map : list) {
            newList.add(_Util.camel(map));
        }
        return newList;
    }
    public static Map<String,Object> camel(Map<String,Object> map){
        Map<String,Object> newMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            String key = e.getKey();
            Object value = e.getValue();
            if(key.contains("_")){
                key = StrUtil.toCamelCase(key);
            }

            newMap.put(key,value);

        }
        return newMap;
    }


}
