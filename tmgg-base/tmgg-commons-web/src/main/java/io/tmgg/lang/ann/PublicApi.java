package io.tmgg.lang.ann;

import java.lang.annotation.*;


/**
 * 表示是否公共api,不设置
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PublicApi {


}
