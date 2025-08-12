package io.tmgg.web.json.ignore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 动态显示字段
 *
 *
 * app端请求时隐藏字段， 如createUser，updateTime等字段
 *
 * 使用示例:
 * ```java
 * {@literal @}JsonIgnoreForApp
 * private String updateUser;
 *```
 * @gendoc
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JsonIgnoreForApp {

}
