package io.tmgg.modules.openapi;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER ,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface OpenApiField {


	boolean required() default true;

	String desc() ;

	/**
	 * 示例值
	 * @return
	 */
	String demo() default "";



}
