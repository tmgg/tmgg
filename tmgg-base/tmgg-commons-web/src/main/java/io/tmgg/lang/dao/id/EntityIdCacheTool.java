package io.tmgg.lang.dao.id;

import io.tmgg.lang.dao.PersistEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityIdCacheTool {
    private static final Map<PersistEntity,String> cache = new ConcurrentHashMap<>();

    public static void cache(PersistEntity entity,String id){
        cache.put(entity,id);
    }

    public static String get(PersistEntity entity){
        String id = cache.get(entity);
        cache.remove(entity);
        return id;
    }

}
