
package io.tmgg.lang;

import lombok.Getter;

/**
 * 业务异常
 *
 */
@Getter
public class CodeException extends RuntimeException {

    private final int code;

    private final String errorMessage;

    public CodeException(int code, String errorMessage) {
        super(errorMessage);
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public CodeException(AbstractBaseExceptionEnum exception) {
        super(exception.getMessage());
        this.code = exception.getCode();
        this.errorMessage = exception.getMessage();
    }
}
