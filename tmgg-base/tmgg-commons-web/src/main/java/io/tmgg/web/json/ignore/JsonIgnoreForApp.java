package io.tmgg.web.json.ignore;

import io.tmgg.web.WebConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 *
 * @doc-title 动态隐藏字段
 * @dot-file feature.md
 * 在非管理后台接口请求时，隐藏字段
 * 只在管理后台渲染json时显示，如createUser，updateTime等字段，不希望app端也能获取
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JsonIgnoreForApp {

    String value() default WebConstants.APP_API;
}
