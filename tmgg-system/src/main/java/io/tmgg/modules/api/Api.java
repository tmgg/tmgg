package io.tmgg.modules.api;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Api {

	/**
	 * Api名称
	 */
	String name();

	/**
	 * Api资源定位
	 */
	String url();


	/**
	 * Api描述
	 */
	String desc() default "";

	/**
	 * 内容是否加密
	 * @return
	 */
	boolean encrypt() default false;

}
