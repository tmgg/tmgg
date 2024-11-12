
package io.tmgg.lang.obj;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应结果数据
 *
 */
@Getter
@Setter
public class AjaxResult  {



    boolean success;

    Integer code;

    Object data;

    String message;

    public AjaxResult() {
       this(true);
    }

    public AjaxResult(boolean success) {
        this.success = success;
    }



    public static AjaxResult ok() {
       return new AjaxResult(true);
    }

    public static AjaxResult err() {
        return new AjaxResult(false);
    }

    public static AjaxResult ok(String msg,Object data) {
      return ok().msg(msg).data(data);
    }

    public static AjaxResult err(String msg) {
        return err().msg(msg);
    }
    public  AjaxResult code(int code){
        this.code = code;
        return this;
    }


    public AjaxResult data(Object data){
        this.data = data;
        return this;
    }

    public AjaxResult msg(String msg){
        this.message = msg;
        return this;
    }


}
