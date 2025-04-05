package io.tmgg.lang.dao.id;

import io.tmgg.lang.dao.PersistEntity;
import lombok.AllArgsConstructor;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 临时短暂存储实体的id
 */
public class EntityIdHolder {

    // 只判断引用相等的map （由于实体id可能设置为空，重写了hashcode，equals方法，使用常规map会导致判断key不准）
    private static final IdentityHashMap<PersistEntity,String> cache = new IdentityHashMap<>();

    public static  void cache(PersistEntity entity, String id) {
       cache.put(entity,id);
    }

    public static  String get(PersistEntity entity) {
       return cache.remove(entity);
    }




}
