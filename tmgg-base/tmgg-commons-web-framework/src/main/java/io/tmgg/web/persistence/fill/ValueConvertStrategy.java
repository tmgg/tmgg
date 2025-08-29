package io.tmgg.web.persistence.fill;


public interface ValueConvertStrategy {


    Object convertValue(Object bean, Object sourceValue, String param);

}
