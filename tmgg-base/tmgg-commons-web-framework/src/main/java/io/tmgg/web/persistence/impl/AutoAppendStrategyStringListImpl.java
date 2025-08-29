package io.tmgg.web.persistence.impl;

import io.tmgg.web.persistence.fill.ValueConvertStrategy;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

@Component
public class AutoAppendStrategyStringListImpl implements ValueConvertStrategy {
    @Override
    public Object convertValue(Object bean, Object sourceValue, String param) {
        String str = (String) sourceValue;

        return StrUtil.split(str, ",");
    }
}
