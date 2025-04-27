package io.tmgg.lang.dao.impl;

import io.tmgg.lang.dao.AutoFillStrategy;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

@Component
public class AutoFillStrategyStringListImpl implements AutoFillStrategy {
    @Override
    public Object getValue(Object bean, Object sourceValue, String param) {
        String str = (String) sourceValue;

        return StrUtil.split(str, ",");
    }
}
