
package io.tmgg.web.validation.flag;

import io.tmgg.web.enums.YesOrNotEnum;
import cn.hutool.core.util.StrUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 校验标识，只有Y和N两种状态的标识
 *
 *
 */
public class FlagValueValidator implements ConstraintValidator<FlagValue, String> {

    private Boolean required;

    @Override
    public void initialize(FlagValue constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String flagValue, ConstraintValidatorContext context) {

        // 如果是必填的
        if (required) {
            return YesOrNotEnum.Y.getCode().equals(flagValue) || YesOrNotEnum.N.getCode().equals(flagValue);
        } else {

            //如果不是必填，可以为空
            if (StrUtil.isEmpty(flagValue)) {
                return true;
            } else {
                return YesOrNotEnum.Y.getCode().equals(flagValue) || YesOrNotEnum.N.getCode().equals(flagValue);
            }
        }
    }
}