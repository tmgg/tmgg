package io.tmgg.job.enums;


import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface JobDesc {

    String name();

    String group() default "";

    JobParamDesc[] params();

}
