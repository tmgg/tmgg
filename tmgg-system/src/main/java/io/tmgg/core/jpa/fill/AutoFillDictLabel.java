package io.tmgg.core.jpa.fill;


import io.tmgg.lang.dao.AutoFill;

import java.lang.annotation.*;

/**
 * 自动填充字段数据字典
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@AutoFill(value = AutoFillDictLabelStrategy.class)
public @interface AutoFillDictLabel {

}
