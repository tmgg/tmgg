
package io.tmgg.web.validation.datetime;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验日期时间格式 yyyy-MM-dd HH:mm:ss
 *
 */
@Documented
@Constraint(validatedBy = DateTimeValueValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimeValue {

    String message() default "日期时间格式不正确，正确格式应为yyyy-MM-dd HH:mm:ss";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        DateTimeValue[] value();
    }
}
