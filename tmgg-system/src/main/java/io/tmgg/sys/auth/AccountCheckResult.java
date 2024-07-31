package io.tmgg.sys.auth;

public enum AccountCheckResult {

    VALID_CODE_ERROR("验证码错误，请检查captcha参数"),
    ACC_PWD_EMPTY("账号或密码为空，请检查account或password参数"),
    ACC_NOT_EXIST("账号不存在"),
    ACC_DISABLED("账号被禁用"),
    PWD_ERROR("密码错误"), VALID("合法");

    String message;

    AccountCheckResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
