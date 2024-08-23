package io.tmgg.flowable.config;

import cn.moon.lang.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class FlowableGlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e) {
        String message = e.getMessage();
        if(message == null|| message.isEmpty()){
            message = "服务器忙";
        }
        return Result.err().msg(message);
    }
}
