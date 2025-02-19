package io.tmgg.web;


public class CodeException extends RuntimeException{
    int code;
    String msg;

    public CodeException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
