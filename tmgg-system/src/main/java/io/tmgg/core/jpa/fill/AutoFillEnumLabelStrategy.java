package io.tmgg.core.jpa.fill;

import io.tmgg.lang.dao.AutoFillStrategy;
import io.tmgg.web.base.MessageEnum;
import org.springframework.stereotype.Component;

@Component
public class AutoFillEnumLabelStrategy implements AutoFillStrategy {


    @Override
    public Object getValue(Object bean, Object sourceValue, String param) {
        if (sourceValue == null) {
            return null;
        }

        if (sourceValue instanceof MessageEnum) {
            MessageEnum me = (MessageEnum) sourceValue;
            return me.getMessage();
        }

        return null;
    }


}
