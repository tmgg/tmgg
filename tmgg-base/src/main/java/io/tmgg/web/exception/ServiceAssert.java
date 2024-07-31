package io.tmgg.web.exception;

import io.tmgg.lang.CodeException;

public class ServiceAssert {
    public static void state(boolean condition, int errorCode, String errorMessage){
        if(!condition){
            throw  new CodeException(errorCode, errorMessage);
        }
    }

    public static void state(boolean condition, String errorMessage){
        if(!condition){
            throw  new CodeException(500, errorMessage);
        }
    }
}
