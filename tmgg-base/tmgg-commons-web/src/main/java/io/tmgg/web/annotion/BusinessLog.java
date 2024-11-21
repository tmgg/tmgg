
package io.tmgg.web.annotion;


import java.lang.annotation.*;

/**
 * 标记需要做业务日志的方法
 *
 * 可放在 controller 和 method 上
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface BusinessLog {

    /**
     * 业务的名称,例如:"修改菜单"
     */
    String value() ;


}
