
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

    // 权限显示的，如增加。 常见的权限显示已经设置，如add,delete
    String title() default "";


}

