package io.tmgg.modules.job;


import io.tmgg.data.FieldDesc;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface JobDesc {

    String name();

    String group() default "默认分组";


    /**
     * 默认定时表达式
     * @return
     */
    String defaultCron() default "0 0 3 * * ?";

    // TODO
    boolean autoPersistent() default false;

    FieldDesc[] params() default {};

    Class<? extends JobParamFieldProvider> paramsProvider() default JobParamFieldProvider.class;

}
