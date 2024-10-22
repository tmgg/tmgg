package io.tmgg.framework.cache;

import javax.cache.CacheManager;
import javax.cache.Caching;

public class CacheTool {

    public static void create(){
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();

    }

}
