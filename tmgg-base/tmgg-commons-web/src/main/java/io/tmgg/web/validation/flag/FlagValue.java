
package io.tmgg.web.validation.flag;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验标识，只有Y和N两种状态的标识
 *
 *
 *
 */
@Documented
@Constraint(validatedBy = FlagValueValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FlagValue {

    String message() default "不正确的状态标识，请传递Y或者N";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 是否必填
     */
    boolean required() default true;

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        FlagValue[] value();
    }
}
