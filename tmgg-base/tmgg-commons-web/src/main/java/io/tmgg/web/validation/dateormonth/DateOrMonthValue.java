
package io.tmgg.web.validation.dateormonth;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验日期格式 yyyy-MM-dd 或 yyyy-MM
 *
 */
@Documented
@Constraint(validatedBy = DateOrMonthValueValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateOrMonthValue {

    String message() default "日期格式不正确，正确格式应为yyyy-MM-dd或yyyy-MM";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        DateOrMonthValue[] value();
    }
}
