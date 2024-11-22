package io.tmgg.lang.dao;


import java.lang.annotation.*;

/**
 * 自动填充字段, 主要针对自定义策略
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Inherited
public @interface AutoFill {

    Class<? extends AutoFillStrategy> value();





    String param() default "";



}
