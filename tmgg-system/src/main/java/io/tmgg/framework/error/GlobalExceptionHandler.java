
package io.tmgg.framework.error;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.ExceptionToMessageTool;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.modules.sys.service.SysConfigService;
import io.tmgg.web.CodeException;
import io.tmgg.web.consts.AopSortConstant;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

/**
 * 全局异常处理器
 */
@Order(AopSortConstant.GLOBAL_EXP_HANDLER_AOP)
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {



    private Boolean isPrintExceptionForAssert;

    @Resource
    private SysConfigService sysConfigService;





    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public AjaxResult noResourceFoundException(NoResourceFoundException e) {
        return AjaxResult.err().code(404).msg("接口或资源不存在 " + e.getMessage());
    }

    /**
     * 请求参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public AjaxResult missParamException(MissingServletRequestParameterException e) {
        log.error(">>> 请求参数异常，具体信息为：{}", e.getMessage());
        String parameterType = e.getParameterType();
        String parameterName = e.getParameterName();
        String message = StrUtil.format(">>> 缺少请求的参数{}，类型为{}", parameterName, parameterType);
        return AjaxResult.err().code(500).msg(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AjaxResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(">>> 请求参数未通过校验：{}", e.getMessage());

        StringBuilder sb = new StringBuilder();
        for (ObjectError error : e.getAllErrors()) {
            sb.append(error.getDefaultMessage()).append(" ");
        }
        return AjaxResult.err().code(500).msg(sb.toString());
    }


    /**
     * 拦截资源找不到的运行时异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AjaxResult notFound(NoHandlerFoundException e) {
        log.error(">>> 资源不存在异常，具体信息为：{}", e.getMessage() + "，请求地址为:" + HttpServletTool.getRequest().getRequestURI());
        return AjaxResult.err().code(404).msg("资源路径不存在，请检查请求地址，请求地址为:" + HttpServletTool.getRequest().getRequestURI());
    }


    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AjaxResult fileNotFoundException(FileNotFoundException e) {
        String uri = HttpServletTool.getRequest().getRequestURI();
        log.error("文件不存在：{} ,请求地址为 {}", e.getMessage() , uri);
        return AjaxResult.err().code(404).msg(e.getMessage()).data("请求路径：" + uri);
    }


    /**
     * 拦截权限异常
     */
    @ExceptionHandler(CodeException.class)
    public AjaxResult systemException(CodeException e) {
        return AjaxResult.err(e.getCode(), e.getCode() + ":" +e.getMessage());
    }


    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public AjaxResult assertError(RuntimeException e) {
        log.error(">>> 业务异常，具体信息为：{}", e.getMessage());
        if(isPrintExceptionForAssert == null){
            isPrintExceptionForAssert = sysConfigService.getBoolean("sys.printAssertException");
        }
        if (isPrintExceptionForAssert) {
            log.error("打印异常已开启,以下是异常详细信息", e);
        }

        return AjaxResult.err().code(500).msg(e.getMessage());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public AjaxResult constraintViolationException(ConstraintViolationException e) {
        log.warn("约束异常:{}", e.getMessage());
        return AjaxResult.err().msg(ExceptionToMessageTool.convert(e));
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public AjaxResult dataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据处理异常", e);
        return AjaxResult.err().msg(ExceptionToMessageTool.convert(e));
    }


    @ExceptionHandler(TransactionSystemException.class)
    public AjaxResult TransactionSystemException(TransactionSystemException e) {
        log.error("事务异常", e);
        return AjaxResult.err().msg(ExceptionToMessageTool.convert(e));
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public AjaxResult InvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e, HttpServletRequest request) {
        e.printStackTrace();
        Throwable throwable = e.getCause();
        return AjaxResult.err().msg(throwable.getMessage());
    }


    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(SQLException.class)
    public AjaxResult sqlException(SQLException e) {
        log.error("SQL异常", e);
        return AjaxResult.err().msg(ExceptionToMessageTool.convert(e));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(Throwable.class)
    public AjaxResult serverError(Throwable e) {
        log.error(">>> 服务器运行异常 ", e);
        return AjaxResult.err().msg(ExceptionToMessageTool.convert(e));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public AjaxResult httpMessageNotReadableException(HttpMessageNotReadableException e) {
        return AjaxResult.err().msg("请求内容错误：" + e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult serverError(HttpRequestMethodNotSupportedException e) {
        return AjaxResult.err().msg("不支持请求方法" + e.getMethod());
    }


    /**
     * 获取请求参数不正确的提示信息
     * <p>
     * 多个信息，拼接成用逗号分隔的形式
     */
    private String getArgNotValidMessage(BindingResult bindingResult) {
        if (bindingResult == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        //多个错误用逗号分隔
        List<ObjectError> allErrorInfos = bindingResult.getAllErrors();
        for (ObjectError error : allErrorInfos) {
            if (error instanceof FieldError) {
                sb.append(((FieldError) error).getField());
            }
            sb.append(error.getDefaultMessage()).append(",");
        }

        //最终把首部的逗号去掉
        return StrUtil.removeSuffix(sb.toString(), ",");
    }



}


