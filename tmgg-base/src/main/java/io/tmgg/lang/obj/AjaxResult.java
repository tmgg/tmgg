
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
        this.setSuccess(success);
        this.setMessage(success ? "操作成功":"操作失败");
    }



    public static AjaxResult ok() {
       return new AjaxResult(true);
    }

    public static AjaxResult err() {
        return new AjaxResult(false);
    }

    public  AjaxResult code(int code){
        this.code = code;
        return this;
    }


    public AjaxResult data(Object data){
        this.setData(data);
        return this;
    }

    public AjaxResult msg(String msg){
        this.setMessage(msg);
        return this;
    }


}
