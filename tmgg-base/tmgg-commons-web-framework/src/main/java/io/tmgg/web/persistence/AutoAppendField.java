package io.tmgg.web.persistence;


import java.lang.annotation.*;

/**
 * 自动增加字段，常用于前端展示
 *
 * 例如有个字段时 orgId, 则前端则会多收到个字段 orgLabel
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface AutoAppendField {

    Class<? extends AutoAppendStrategy> value();


    boolean removeIdStr() default true;

    String suffix() default "Label";

    String param() default "";
}
