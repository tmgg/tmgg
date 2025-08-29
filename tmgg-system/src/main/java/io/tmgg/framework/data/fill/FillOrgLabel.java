package io.tmgg.framework.data.fill;


import io.tmgg.framework.data.append.AutoAppendDictLabelStrategy;
import io.tmgg.framework.data.append.AutoAppendOrgLabelStrategy;
import io.tmgg.web.persistence.fill.FillField;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@FillField(strategy = AutoAppendOrgLabelStrategy.class)
public @interface FillOrgLabel {



}
