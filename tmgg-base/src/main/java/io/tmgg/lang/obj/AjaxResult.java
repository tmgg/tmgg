
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




    public static final Integer DEFAULT_SUCCESS_CODE = 200;

    public static final Integer DEFAULT_ERROR_CODE = 500;

    public static final String SUCCESS_MESSAGE = "操作成功";
    public static final String ERROR_MESSAGE = "操作失败";

    boolean success;

    Integer code;

    Object data;

    String message;

    public AjaxResult() {
       this(true);
    }

    public AjaxResult(boolean success) {
        this.setSuccess(success);
    }

    @Deprecated
    public static AjaxResult success() {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMessage(SUCCESS_MESSAGE);
        return ajaxResult;
    }

    @Deprecated
    public static AjaxResult success(Object data) {
        AjaxResult rs = AjaxResult.success();
        rs.setData(data);
        return rs;
    }

    @Deprecated
    public static AjaxResult success(String msg, Object data){
        AjaxResult rs = AjaxResult.success();
        rs.setData(data);
        rs.setMessage(msg);
        return rs;
    }

    @Deprecated
    public static AjaxResult error() {
        AjaxResult rs = AjaxResult.success();
        rs.setMessage(ERROR_MESSAGE);
        rs.setSuccess(false);
        rs.setCode(DEFAULT_ERROR_CODE);
        return rs;
    }

    @Deprecated
    public static AjaxResult error(String message) {
        AjaxResult rs = AjaxResult.success();
        rs.setMessage(message);
        rs.setSuccess(false);
        rs.setCode(DEFAULT_ERROR_CODE);
        return rs;
    }

    @Deprecated
    public static AjaxResult error(Integer code, String message) {
        AjaxResult rs = AjaxResult.success();
        rs.setMessage(message);
        rs.setSuccess(false);
        rs.setCode(code);
        return rs;
    }

    @Deprecated
    public static AjaxResult error(Integer code, String message, Object data) {
        AjaxResult rs = AjaxResult.success();
        rs.setMessage(message);
        rs.setSuccess(false);
        rs.setCode(code);
        rs.setData(data);
        return rs;
    }


    public static AjaxResult ok() {
        AjaxResult rs = new AjaxResult();
        rs.setMessage(SUCCESS_MESSAGE);
        return rs;
    }

    public static AjaxResult err() {
        AjaxResult rs = AjaxResult.success();
        rs.setMessage(ERROR_MESSAGE);
        rs.setSuccess(false);
        rs.setCode(DEFAULT_ERROR_CODE);
        return rs;
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
