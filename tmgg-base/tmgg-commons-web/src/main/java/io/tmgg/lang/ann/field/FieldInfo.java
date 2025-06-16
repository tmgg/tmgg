package io.tmgg.lang.ann.field;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER ,ElementType.FIELD,ElementType.ANNOTATION_TYPE})


@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface FieldInfo {

	String name() default "";

	boolean required() default true;

	String label() ;

	/**
	 * 示例值
	 * @return
	 */
	String demo() default "";

	int len() default -1;

	/**
	 * 字段类型
	 * @return
	 */
	FieldType fieldType() default FieldType.STRING;



}
