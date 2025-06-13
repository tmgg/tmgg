package io.tmgg.modules.job;


import io.tmgg.lang.ann.field.FieldInfo;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface JobDesc {

    String label();

    String group() default "默认分组";


    FieldInfo[] params() default {};

    /**
     * 动态参数
     * @return
     */
    Class<? extends JobParamFieldProvider> paramsProvider() default JobParamFieldProvider.class;

}
