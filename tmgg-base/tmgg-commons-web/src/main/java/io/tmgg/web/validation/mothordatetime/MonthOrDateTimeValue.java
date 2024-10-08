
package io.tmgg.web.validation.mothordatetime;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验日期格式 yyyy-MM 或 yyyy-MM-dd HH:mm:ss
 *
 */
@Documented
@Constraint(validatedBy = MonthOrDateTimeValueValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MonthOrDateTimeValue {

    String message() default "日期格式不正确，正确格式应为yyyy-MM或yyyy-MM-dd HH:mm:ss";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        MonthOrDateTimeValue[] value();
    }
}
