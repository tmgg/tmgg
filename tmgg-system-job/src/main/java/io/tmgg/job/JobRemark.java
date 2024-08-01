package io.tmgg.job;


import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface JobRemark {

    Param[] params();

}
