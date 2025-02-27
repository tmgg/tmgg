package io.tmgg.modules.openapi;

import lombok.Data;

@Data
public class ApiResult {
    int code;
    String msg;
    String data;

    public ApiResult() {
    }

    public ApiResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResult(int code, String msg, String data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
