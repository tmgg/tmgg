
package io.tmgg.web.validation.dateortime;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验日期或时间格式 yyyy-MM-dd 或 HH:mm:ss
 *
 */
@Documented
@Constraint(validatedBy = DateOrTimeValueValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateOrTimeValue {

    String message() default "日期或时间格式不正确，正确格式应为yyyy-MM-dd 或 HH:mm:ss";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        DateOrTimeValue[] value();
    }
}
