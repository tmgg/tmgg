package io.tmgg.web.persistence.impl;

import io.tmgg.web.persistence.AutoAppendStrategy;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

@Component
public class AutoAppendStrategyStringListImpl implements AutoAppendStrategy {
    @Override
    public Object getAppendValue(Object bean, Object sourceValue, String param) {
        String str = (String) sourceValue;

        return StrUtil.split(str, ",");
    }
}
