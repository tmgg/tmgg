package io.tmgg.core.jpa.fill;


import io.tmgg.lang.dao.AutoFill;

import java.lang.annotation.*;

/**
 * 自动填充文件url
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@AutoFill(value = AutoFillUserLabelStrategy.class)
public @interface AutoFillFileUrl {

}
