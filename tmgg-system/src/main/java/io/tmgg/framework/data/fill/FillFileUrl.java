package io.tmgg.framework.data.fill;

import io.tmgg.framework.data.append.AutoAppendFileViewUrlStrategy;
import io.tmgg.web.persistence.fill.FillField;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@FillField(strategy = AutoAppendFileViewUrlStrategy.class)
public @interface FillFileUrl {
}
