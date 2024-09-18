package io.tmgg.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SystemException extends RuntimeException{

    int code;
    String message;

}
