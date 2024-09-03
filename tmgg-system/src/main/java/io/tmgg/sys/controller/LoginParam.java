package io.tmgg.sys.controller;

import lombok.Data;

@Data
public class LoginParam {
    String account;
    String password;
    String code;

    // 外部系统登录的token
    String token;

    String clientId; // 客户端标识，主要用于验证码二次验证
}
