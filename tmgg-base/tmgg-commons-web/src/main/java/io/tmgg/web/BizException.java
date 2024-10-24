package io.tmgg.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BizException extends RuntimeException{



    String message;

    public BizException() {
    }

    public BizException(String message) {
        super(message);
        this.message = message;
    }
}
