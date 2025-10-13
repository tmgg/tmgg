package io.tmgg.web.persistence.fill;

/**
 * 接口实现如果使用了jpa，可能引起死循环，可使用dbTool
 */
public interface ValueConvertStrategy {


    Object convertValue(Object bean, Object sourceValue, String param);

}
