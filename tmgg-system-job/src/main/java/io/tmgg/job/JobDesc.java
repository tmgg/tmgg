package io.tmgg.job;


import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface JobDesc {

    String name();

    String group() default "";



    JobParamAnn[] params();

    Class<? extends JobParamFieldProvider> paramsProvider() default JobParamFieldProvider.class;

}
