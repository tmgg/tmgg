package io.tmgg.lang;


import org.apache.commons.lang3.ArrayUtils;

public class AssertTool {

    public static void state(boolean state, String errMsg) {
        if (!state) {
            throw new CodeException(500, errMsg);
        }
    }


    public static void in(Object target, Object[] arr,  String errMsg) {
        if (!ArrayUtils.contains(arr, target)) {
            throw new CodeException(500, errMsg);
        }
    }

    public static void notNull(Object obj, String errMsg){
        if(obj == null){
            throw new CodeException(500, errMsg);
        }
    }
}
