package io.tmgg.web;

import org.springframework.util.Assert;

public class BizException extends IllegalStateException {

    private int code;

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String prefixMessage, Throwable e) {
        super(prefixMessage + ": " + e.getMessage());
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
