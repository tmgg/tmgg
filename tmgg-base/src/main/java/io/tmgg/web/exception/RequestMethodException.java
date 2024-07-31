
package io.tmgg.web.exception;

import io.tmgg.lang.AbstractBaseExceptionEnum;
import lombok.Getter;

/**
 * 请求方法异常
 *

 *
 */
@Getter
public class RequestMethodException extends RuntimeException {

    private final int code;

    private final String errorMessage;

    public RequestMethodException(AbstractBaseExceptionEnum exception) {
        super(exception.getMessage());
        this.code = exception.getCode();
        this.errorMessage = exception.getMessage();
    }
}
