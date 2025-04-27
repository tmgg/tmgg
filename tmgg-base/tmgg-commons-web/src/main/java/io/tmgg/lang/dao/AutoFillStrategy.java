package io.tmgg.lang.dao;

/**
 * 自动填充字段策略
 *
 * @author jiangtao
 */
public interface AutoFillStrategy {


    Object getValue(Object bean, Object sourceValue, String param);

}
