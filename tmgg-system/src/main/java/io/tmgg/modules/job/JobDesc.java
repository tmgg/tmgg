package io.tmgg.modules.job;


import io.tmgg.lang.field.FieldInfo;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface JobDesc {

    String label();

    String group() default "默认分组";


    FieldInfo[] params() default {};

    Class<? extends JobParamFieldProvider> paramsProvider() default JobParamFieldProvider.class;

}
