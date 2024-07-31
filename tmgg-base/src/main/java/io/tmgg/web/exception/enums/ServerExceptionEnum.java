
package io.tmgg.web.exception.enums;

import io.tmgg.lang.AbstractBaseExceptionEnum;
import io.tmgg.web.annotion.ExpEnumType;
import io.tmgg.web.consts.ExpEnumConstant;
import io.tmgg.web.factory.ExpEnumCodeFactory;

/**
 * 服务器内部相关异常枚举
 *

 *
 */
@ExpEnumType(module = ExpEnumConstant.SNOWY_CORE_MODULE_EXP_CODE, kind = ExpEnumConstant.SERVER_EXCEPTION_ENUM)
public enum ServerExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 当前请求参数为空或数据缺失
     */
    REQUEST_EMPTY(1, "当前请求参数为空或数据缺失，请联系管理员"),

    /**
     * 服务器出现未知异常
     */
    SERVER_ERROR(2, "服务器忙，请稍后再试"),

    /**
     * 常量获取存在空值
     */
    CONSTANT_EMPTY(3, "常量获取存在空值，请检查sys_config中是否配置");

    private final int code;

    private final String message;

    ServerExceptionEnum(int code, String message) {
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
