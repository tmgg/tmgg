
package io.tmgg.web.exception.enums;

import io.tmgg.lang.AbstractBaseExceptionEnum;
import io.tmgg.web.annotion.ExpEnumType;
import io.tmgg.web.consts.ExpEnumConstant;
import io.tmgg.web.factory.ExpEnumCodeFactory;

/**
 * 请求方法相关异常枚举
 *
 */
@ExpEnumType(module = ExpEnumConstant.SNOWY_CORE_MODULE_EXP_CODE, kind = ExpEnumConstant.REQUEST_METHOD_EXCEPTION_ENUM)
public enum RequestMethodExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 不支持该请求方法，请求方法应为POST
     */
    REQUEST_METHOD_IS_POST(1, "不支持该请求方法，请求方法应为POST"),

    /**
     * 不支持该请求方法，请求方法应为GET
     */
    REQUEST_METHOD_IS_GET(2, "不支持该请求方法，请求方法应为GET");

    private final int code;

    private final String message;

    RequestMethodExceptionEnum(int code, String message) {
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
