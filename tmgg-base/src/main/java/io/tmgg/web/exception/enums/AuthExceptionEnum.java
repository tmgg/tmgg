
package io.tmgg.web.exception.enums;

import io.tmgg.lang.AbstractBaseExceptionEnum;
import io.tmgg.web.annotion.ExpEnumType;
import io.tmgg.web.consts.ExpEnumConstant;
import io.tmgg.web.factory.ExpEnumCodeFactory;

/**
 * 认证相关的异常的枚举
 * <p>
 * 认证和鉴权的区别：
 * <p>
 * 认证可以证明你能登录系统，认证的过程是校验token的过程
 * 鉴权可以证明你有系统的哪些权限，鉴权的过程是校验角色是否包含某些接口的权限
 *
 *
 *
 */
@ExpEnumType(module = ExpEnumConstant.SNOWY_CORE_MODULE_EXP_CODE, kind = ExpEnumConstant.AUTH_EXCEPTION_ENUM)
public enum AuthExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 账号或密码为空
     */
    ACCOUNT_PWD_EMPTY(1, "账号或密码为空，请检查account或password参数"),

    /**
     * 账号密码错误
     */
    ACCOUNT_PWD_ERROR(2, "账号或密码错误，请检查account或password参数"),

    /**
     * 验证码错误
     */
    VALID_CODE_ERROR(3, "验证码错误，请检查captcha参数"),

    /**
     * 请求token为空
     */
    REQUEST_TOKEN_EMPTY(4, "请求token为空，请携带token访问本接口"),

    /**
     * token格式不正确，token请以Bearer开头
     */
    NOT_VALID_TOKEN_TYPE(5, "token格式不正确，token请以Bearer开头，并且Bearer后边带一个空格"),

    /**
     * 请求token错误
     */
    REQUEST_TOKEN_ERROR(6, "登录失效，请重新登录"),

    /**
     * 账号被冻结
     */
    ACCOUNT_FREEZE_ERROR(7, "账号被冻结，请联系管理员"),

    /**
     * 登录已过期
     */
    LOGIN_EXPIRED(8, "登录已过期，请重新登录"),

    /**
     * 无登录用户
     */
    NO_LOGIN_USER(9, "无登录用户"),

    /**
     * 验证码错误
     */
    CONSTANT_EMPTY_ERROR(10, "验证码错误");

    private final int code;

    private final String message;

    AuthExceptionEnum(int code, String message) {
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
