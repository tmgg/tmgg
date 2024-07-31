
package io.tmgg.core.cache;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Set;

/**
 * 项目资源的缓存，存储了项目所有的访问url
 * <p>
 * 一般用在过滤器检测请求是否是项目没有的url
 *
 *
 *
 */
public class ResourceCache {

    private final Set<String> resourceCaches = CollectionUtil.newHashSet();

    /**
     * 获取所有缓存资源
     *
     *
 *
     */
    public Set<String> getAllResources() {
        return resourceCaches;
    }

    /**
     * 直接缓存所有资源
     *
     *
 *
     */
    public void putAllResources(Set<String> resources) {
        resourceCaches.addAll(resources);
    }

}
