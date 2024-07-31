
package io.tmgg.web.exception;

import lombok.Getter;

/**
 * 授权和鉴权异常
 */
@Getter
public class PermissionException extends RuntimeException {

    public PermissionException(String msg) {
        super(msg);
    }
}
