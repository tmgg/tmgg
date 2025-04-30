package io.tmgg.web;

public class BizAssert {

    public  static final int DEFAULT_ERROR_CODE = 1000;

    public static void state(boolean state,  String msg){
        if(!state){
            throw new BizException(DEFAULT_ERROR_CODE,msg);
        }
    }
    public static void state(boolean state, int code, String msg){
        if(!state){
            throw new BizException(code,msg);
        }
    }
}
