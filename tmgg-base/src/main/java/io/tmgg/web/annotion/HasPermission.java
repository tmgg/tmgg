
package io.tmgg.web.annotion;

import java.lang.annotation.*;

/**
 * 权限注解，用于检查权限
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface HasPermission {

    String value() default "";

}

