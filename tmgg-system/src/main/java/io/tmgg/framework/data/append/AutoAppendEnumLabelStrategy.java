package io.tmgg.framework.data.append;

import io.tmgg.persistence.AutoAppendStrategy;
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
