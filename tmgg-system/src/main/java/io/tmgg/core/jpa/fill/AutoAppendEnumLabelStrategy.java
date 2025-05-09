package io.tmgg.core.jpa.fill;

import io.tmgg.lang.dao.AutoAppendStrategy;
import io.tmgg.web.base.DictEnum;
import org.springframework.stereotype.Component;

@Component
public class AutoAppendEnumLabelStrategy implements AutoAppendStrategy {


    @Override
    public Object getAppendValue(Object bean, Object sourceValue, String param) {
        if (sourceValue == null) {
            return null;
        }

        if (sourceValue instanceof DictEnum) {
            DictEnum me = (DictEnum) sourceValue;
            return me.getMessage();
        }

        return null;
    }


}
