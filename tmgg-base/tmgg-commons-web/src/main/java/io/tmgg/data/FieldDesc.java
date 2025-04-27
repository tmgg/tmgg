package io.tmgg.data;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface FieldDesc {

    String name();
    String label();

    boolean required() default false;
}
