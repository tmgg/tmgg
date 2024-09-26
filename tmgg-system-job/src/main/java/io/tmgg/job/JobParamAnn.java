package io.tmgg.job;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface JobParamAnn {

    String name();
    String label();

    boolean required() default false;
}
