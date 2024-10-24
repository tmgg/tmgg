package io.tmgg.web;

public class BizException extends RuntimeException {


    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(Throwable e) {
        this("服务异常", e);
    }

    public BizException(String prefixMessage, Throwable e) {
        super(prefixMessage + ": " + e.getMessage());
    }
}
