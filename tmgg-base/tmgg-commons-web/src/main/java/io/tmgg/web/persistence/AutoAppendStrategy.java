package io.tmgg.web.persistence;

/**
 * 自动填充字段策略
 *
 * @author jiangtao
 */
public interface AutoAppendStrategy {


    Object getAppendValue(Object bean, Object sourceValue, String param);

}
