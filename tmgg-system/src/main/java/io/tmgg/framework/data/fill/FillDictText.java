package io.tmgg.framework.data.fill;


import io.tmgg.framework.data.append.AutoAppendDictLabelStrategy;
import io.tmgg.web.persistence.fill.FillField;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@FillField(strategy = AutoAppendDictLabelStrategy.class)
public @interface FillDictText {

    @AliasFor(annotation = FillField.class, attribute = "params")
    String typeCode();

}
