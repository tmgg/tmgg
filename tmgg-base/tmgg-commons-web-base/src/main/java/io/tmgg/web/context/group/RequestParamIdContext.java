
package io.tmgg.web.context.group;

/**
 * 临时保存参数id字段值，用于唯一性校验
 * 注意：如果要用@TableUniqueValue这个校验，必须得主键的字段名是id，否则会校验失败
 */
public class RequestParamIdContext {

    private static final ThreadLocal<String> PARAM_ID_HOLDER = new ThreadLocal<>();

    /**
     * 设置id
     */
    public static void set(String id) {
        PARAM_ID_HOLDER.set(id);
    }

    /**
     * 获取id
     */
    public static String get() {
        return PARAM_ID_HOLDER.get();
    }

    /**
     * 清除缓存id
     */
    public static void clear() {
        PARAM_ID_HOLDER.remove();
    }

}
