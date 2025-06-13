package io.tmgg.lang.ann.field;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER ,ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface FieldInfos {

	FieldInfo[] value() default {};



}
