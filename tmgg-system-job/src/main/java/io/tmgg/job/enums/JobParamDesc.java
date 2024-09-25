package io.tmgg.job.enums;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface JobParamDesc {

    String key();
    String label();

    boolean required() default false;
}
