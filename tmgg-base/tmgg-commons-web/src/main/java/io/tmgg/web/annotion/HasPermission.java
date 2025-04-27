
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

    /**
     * 保存日志
     * @return
     */
    boolean log() default true;

    String label() default "";
}

