package io.tmgg.job;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Param {

    String key();
    String label();

    boolean required() default false;
}
