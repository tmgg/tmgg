package io.tmgg.lang.ann;

import java.lang.annotation.*;


/**
 * 表示是否公共请求，忽略系统默认的登录，鉴权
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PublicRequest {


}
