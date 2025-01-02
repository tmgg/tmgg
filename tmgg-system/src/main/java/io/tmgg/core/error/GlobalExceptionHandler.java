
package io.tmgg.core.error;

import cn.hutool.core.util.StrUtil;
import io.tmgg.SysProp;
import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.RegexTool;
import io.tmgg.lang.StrTool;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.BizException;
import io.tmgg.web.consts.AopSortConstant;
import jakarta.annotation.Resource;
import jakarta.persistence.RollbackException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * 全局异常处理器
 */
@Order(AopSortConstant.GLOBAL_EXP_HANDLER_AOP)
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @Resource
    SysProp sysProp;


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


    /**
     * 拦截资源找不到的运行时异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AjaxResult notFound(NoHandlerFoundException e) {
        log.error(">>> 资源不存在异常，具体信息为：{}", e.getMessage() + "，请求地址为:" + HttpServletTool.getRequest().getRequestURI());
        return AjaxResult.err().code(404).msg("资源路径不存在，请检查请求地址，请求地址为:" + HttpServletTool.getRequest().getRequestURI());
    }


    /**
     * 拦截权限异常
     */
    @ExceptionHandler(BizException.class)
    public AjaxResult systemException(BizException e) {
        return AjaxResult.err().msg(e.getMessage());
    }


    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public AjaxResult assertError(RuntimeException e) {
        log.error(">>> 业务异常，assertError ，具体信息为：{}", e.getMessage());
        if (sysProp.isPrintException()) {
            e.printStackTrace();
        }

        return AjaxResult.err().code(500).msg(e.getMessage());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public AjaxResult constraintViolationException(ConstraintViolationException e) {
        log.warn("约束异常:{}", e.getMessage());
        return AjaxResult.err().msg(getHumanMessage(e));
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public AjaxResult dataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        log.error("数据处理异常", e);

        if (e != null && e.getCause() != null && e.getCause().getCause() != null) {
            Throwable ex = e.getCause().getCause();
            String msg = ex.getMessage();

            if (msg.contains("Data too long")) {
                return AjaxResult.err().msg("数据长度超过限制，请修改！");
            }


            if (ex instanceof SQLIntegrityConstraintViolationException) {
                if (msg.startsWith("Duplicate")) {
                    String result = RegexTool.findFirstMatch("\\'(.*?)\\'", msg, 1);
                    if (StrUtil.isNotBlank(result)) {
                        return AjaxResult.err().msg("数据重复,操作不能继续进行。 重复数据为：" + result);
                    }
                }

                {
                    // Column 'file_id' cannot be null
                    String regex = "Column '(.*)' cannot be null";
                    String fieldName = RegexTool.findFirstMatch(regex, msg, 1);
                    if (StrUtil.isNotEmpty(fieldName)) {
                        return AjaxResult.err().msg("字段" + fieldName + "不能为空");
                    }
                }

            }
        }

        return AjaxResult.err().msg("违反数据库规则，操作不能继续进行");
    }


    @ExceptionHandler(TransactionSystemException.class)
    public AjaxResult TransactionSystemException(TransactionSystemException e, HttpServletRequest request) {
        log.error("事务异常", e);
        if (e.getCause() != null) {
            RollbackException cause = (RollbackException) e.getCause();
            if (cause != null) {
                Throwable cause2 = cause.getCause();
                if (cause2 instanceof ConstraintViolationException) {
                    String humanMessage = getHumanMessage((ConstraintViolationException) cause2);
                    return AjaxResult.err().msg(humanMessage);
                }
            }
        }
        return AjaxResult.err().msg("网络异常,请稍后再试");
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
        return renderException(e);
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult serverError(Throwable e) {
        log.error(">>> 服务器运行异常 ", e);
        return renderException(e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult httpMessageNotReadableException(HttpMessageNotReadableException e) {
        return AjaxResult.err().msg("请求体内容不可完整读取" + e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult serverError(HttpRequestMethodNotSupportedException e) {
        return AjaxResult.err().msg("不支持请求方法" + e.getMethod());
    }

    /**
     * 渲染异常json
     */
    private AjaxResult renderException(Throwable throwable) {
        String message = throwable.getMessage();

        // 中文则提示中文，非中文则使用默认提示
        if (!StrTool.isChinese(message)) {
            message = "服务异常";
        }

        return AjaxResult.err().code(500).msg(message);
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


    private String getHumanMessage(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        StringBuilder sb = new StringBuilder();

        for (ConstraintViolation<?> v : constraintViolations) {
            String property = v.getPropertyPath().toString();
            String message = v.getMessage();
            sb.append(property);
            sb.append(message);
            sb.append(" \r\n");
        }

        return sb.toString();
    }
}


