package io.tmgg.web.jwt;


public class JwtNotFountException extends IllegalStateException {
    public JwtNotFountException() {
        super("未找到登录凭证(jwt is null)");
    }
}

