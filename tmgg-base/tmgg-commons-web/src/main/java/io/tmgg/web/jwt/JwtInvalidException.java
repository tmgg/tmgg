package io.tmgg.web.jwt;


public class JwtInvalidException extends IllegalStateException {
    public JwtInvalidException(String msg) {
        super(msg);
    }
}
