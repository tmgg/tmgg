package io.tmgg.lang.dao.impl;

import io.tmgg.lang.dao.AutoAppendStrategy;
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
