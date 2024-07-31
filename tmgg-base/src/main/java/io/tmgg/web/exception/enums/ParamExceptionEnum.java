
package io.tmgg.web.exception.enums;

import io.tmgg.lang.AbstractBaseExceptionEnum;
import io.tmgg.web.annotion.ExpEnumType;
import io.tmgg.web.consts.ExpEnumConstant;
import io.tmgg.web.factory.ExpEnumCodeFactory;

/**
 * 参数校验异常枚举
 *

 *
 */
@ExpEnumType(module = ExpEnumConstant.SNOWY_CORE_MODULE_EXP_CODE, kind = ExpEnumConstant.PARAM_EXCEPTION_ENUM)
public enum ParamExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 参数错误
     */
    PARAM_ERROR(1, "参数错误");

    private final int code;

    private final String message;

    ParamExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return ExpEnumCodeFactory.getExpEnumCode(this.getClass(), code);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
