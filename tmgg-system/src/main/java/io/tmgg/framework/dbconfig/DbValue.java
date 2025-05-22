package io.tmgg.framework.dbconfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 系统参数注解 @DbValue
 *
 * 数据库有sys_config表，可使用注解 @DbValue，使用方法类似Spring的@Value注解。
 *
 * 如果在系统中修改了参数，也会实时重新修改字段的值
 *
 * ```
 * {@literal @}DbValue("sys.sessionIdleTime")
 * private int timeToIdleExpiration;
 * ```
 * @gendoc
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DbValue {

    /**
     * 配置项的键（对应 sys_config 表中的 id）
      */
    String value();

}
