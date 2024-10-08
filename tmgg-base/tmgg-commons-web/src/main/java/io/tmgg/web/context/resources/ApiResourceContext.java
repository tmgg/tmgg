
package io.tmgg.web.context.resources;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;

import java.util.Set;

/**
 * 存放本系统所有@RequestMapping的Url
 *
 *
 *
 */
public class ApiResourceContext {

    /**
     * 所有资源的url
     */
    private static final Set<String> API_URLS = CollectionUtil.newHashSet();

    /**
     * 添加一批url
     *
     *
 *
     */
    public static void addBatchUrls(Set<String> urls) {
        if (ObjectUtil.isEmpty(urls)) {
            return;
        }
        API_URLS.addAll(urls);
    }

    /**
     * 添加url
     *
     *
 *
     */
    public static void addUrl(String url) {
        if (ObjectUtil.isEmpty(url)) {
            return;
        }
        API_URLS.add(url);
    }

    /**
     * 删除url
     *
     *
 *
     */
    public static void deleteUrl(String url) {
        if (ObjectUtil.isEmpty(url)) {
            return;
        }
        API_URLS.remove(url);
    }

    /**
     * 获取系统的所有url
     *
     *
 *
     */
    public static Set<String> getApiUrls() {
        return API_URLS;
    }
}
