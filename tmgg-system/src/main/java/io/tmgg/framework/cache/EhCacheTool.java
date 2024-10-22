package io.tmgg.framework.cache;

import javax.cache.CacheManager;
import javax.cache.Caching;

public class EhCacheTool {

    public static void create(){
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();

    }

}
