package io.tmgg.modules.base;

import lombok.Data;

@Data
public class LoginParam {
    String account;
    String password;
    // 验证码
    String code;

    // 外部系统登录的token
    String token;

    String clientId; // 客户端标识，主要用于验证码二次验证


}
