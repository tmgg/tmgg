
package io.tmgg.sys.user.enums;

import io.tmgg.web.annotion.ExpEnumType;
import io.tmgg.lang.AbstractBaseExceptionEnum;
import io.tmgg.web.factory.ExpEnumCodeFactory;
import io.tmgg.core.consts.SysExpEnumConstant;

/**
 * 系统用户相关异常枚举
 *

 *
 */
@ExpEnumType(module = SysExpEnumConstant.SNOWY_SYS_MODULE_EXP_CODE, kind = SysExpEnumConstant.SYS_USER_EXCEPTION_ENUM)
public enum SysUserExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 用户不存在
     */
    USER_NOT_EXIST(1, "用户不存在"),

    /**
     * 账号已存在
     */
    USER_ACCOUNT_REPEAT(2, "账号已存在，请检查account参数");

    private final int code;

    private final String message;

    SysUserExceptionEnum(int code, String message) {
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
