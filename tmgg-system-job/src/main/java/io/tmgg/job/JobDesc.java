package io.tmgg.job;


import io.tmgg.data.FieldAnn;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface JobDesc {

    String name();

    String group() default "";



    FieldAnn[] params();

    Class<? extends JobParamFieldProvider> paramsProvider() default JobParamFieldProvider.class;

}
