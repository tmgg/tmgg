package io.tmgg.persistence;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 和实体（orm实体）相关的工具类
 */
public class EntityTool {

    public static <T extends PersistEntity> Map<String, T> toMap(List<T> list) {
        Map<String, T> map = new HashMap<>();

        for (T t : list) {
            String id = t.getId();
            map.put(id, t);
        }

        return map;
    }

    public static <T > Map<Object, T> toMap(List<T> list, Function<T, Object>  keyFn) {
        Map<Object, T> map = new HashMap<>();

        for (T t : list) {
            Object id = keyFn.apply(t);
            if(id != null){
                map.put(id, t);
            }
        }

        return map;
    }


    /**
     * 复制属性， 排除 id，createTime 等 自动生成的属性
     *
     * @param source 原始对象
     * @param target 目标
     * @return 目标
     * @throws BeansException
     */
    public static <T> T copyIgnoreBaseKeys(Object source, T target) throws BeansException {
        BeanUtils.copyProperties(source, target, BaseEntity.BASE_ENTITY_FIELDS);
        return target;
    }


    public static <T extends PersistEntity> T of(Class<T> cls, String id) {
        if (StrUtil.isEmpty(id)) {
            return null;
        }
        try {
            T t = cls.getDeclaredConstructor().newInstance();
            t.setId(id);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends PersistEntity> List<T> of(Class<T> cls, Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        return ids.stream().map(id -> of(cls, id)).collect(Collectors.toList());
    }


    public static <T extends PersistEntity> List<T> of(Class<T> cls, String[] ids) {
        if (ids == null || ids.length == 0) {
            return new ArrayList<>();
        }
        return Arrays.stream(ids).map(id -> of(cls, id)).collect(Collectors.toList());
    }


}
