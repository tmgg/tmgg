package io.tmgg.web.annotion;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface HasRole {

    String value() default "superAdmin";
}
