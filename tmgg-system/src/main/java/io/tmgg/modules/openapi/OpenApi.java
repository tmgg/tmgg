package io.tmgg.modules.openapi;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface OpenApi {

	/**
	 * Api名称
	 */
	String name();

	/**
	 * Api资源定位
	 */
	String action();


	/**
	 * Api描述
	 */
	String desc() default "";



}
