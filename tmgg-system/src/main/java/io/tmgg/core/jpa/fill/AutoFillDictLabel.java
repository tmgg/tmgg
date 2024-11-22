package io.tmgg.core.jpa.fill;


import io.tmgg.lang.dao.AutoFill;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * 自动填充字段数据字典
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@AutoFill(value = AutoFillDictLabelAutoFillStrategy.class)
public @interface AutoFillDictLabel {

}
