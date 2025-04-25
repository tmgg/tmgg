package io.tmgg.framework.dbconfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DbValue {

    /**
     * 配置项的键（对应 sys_config 表中的 id）
      */
    String value();

}
