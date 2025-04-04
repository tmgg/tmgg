package io.tmgg.lang.dao.id;

import io.tmgg.lang.dao.PersistEntity;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * 临时短暂存储实体的id
 */
public class EntityIdHolder {

    private static final List<Entry> list = new LinkedList<>();

    public static synchronized void cache(PersistEntity entity, String id) {
        list.add(new Entry(entity,id));
    }

    public static synchronized String get(Object entity) {
        Entry entry = list.stream().filter(t -> t.entity == entity).findFirst().orElse(null); // 效率不高，但量少，勉强这样吧
        if(entry == null){
            return null;
        }
        list.remove(entry);

        return entry.id;
    }


    @AllArgsConstructor
    static class Entry {
        PersistEntity entity;
        String id;
    }

}
