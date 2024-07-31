
package io.tmgg.core.error;

import io.tmgg.SystemProperties;
import io.tmgg.lang.*;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.consts.AopSortConstant;
import io.tmgg.web.context.requestno.RequestNoContext;
import io.tmgg.web.exception.PermissionException;
import io.tmgg.web.exception.enums.*;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.annotation.Resource;
import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * 全局异常处理器
 *
 */
@Order(AopSortConstant.GLOBAL_EXP_HANDLER_AOP)
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @Resource
    SystemProperties systemProperties;


    /**
     * 请求参数缺失异常
     *
 *
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public AjaxResult missParamException(MissingServletRequestParameterException e) {
        log.error(">>> 请求参数异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
        String parameterType = e.getParameterType();
        String parameterName = e.getParameterName();
        String message = StrUtil.format(">>> 缺少请求的参数{}，类型为{}", parameterName, parameterType);
        return AjaxResult.error(500, message);
    }

    /**
     * 拦截参数格式传递异常
     *
 *
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public AjaxResult httpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error(">>> 参数格式传递异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
        return renderJson(RequestTypeExceptionEnum.REQUEST_JSON_ERROR);
    }

    /**
     * 拦截不支持媒体类型异常
     *
 *
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public AjaxResult httpMediaTypeNotSupport(HttpMediaTypeNotSupportedException e) {
        log.error(">>> 参数格式传递异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
        return renderJson(RequestTypeExceptionEnum.REQUEST_TYPE_IS_JSON);
    }

    /**
     * 拦截请求方法的异常
     *
 *
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult methodNotSupport(HttpServletRequest request) {
        if (ServletUtil.isPostMethod(request)) {
            log.error(">>> 请求方法异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), RequestMethodExceptionEnum.REQUEST_METHOD_IS_GET.getMessage());
            return renderJson(RequestMethodExceptionEnum.REQUEST_METHOD_IS_GET);
        }
        if (ServletUtil.isGetMethod(request)) {
            log.error(">>> 请求方法异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), RequestMethodExceptionEnum.REQUEST_METHOD_IS_POST.getMessage());
            return renderJson(RequestMethodExceptionEnum.REQUEST_METHOD_IS_POST);
        }
        return null;
    }

    /**
     * 拦截资源找不到的运行时异常
     *
 *
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AjaxResult notFound(NoHandlerFoundException e) {
        log.error(">>> 资源不存在异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage() + "，请求地址为:" + HttpServletTool.getRequest().getRequestURI());
        return AjaxResult.error(404, "资源路径不存在，请检查请求地址，请求地址为:" + HttpServletTool.getRequest().getRequestURI());
    }

    /**
     * 拦截参数校验错误异常,JSON传参
     *
 *
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AjaxResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String argNotValidMessage = getArgNotValidMessage(e.getBindingResult());
        log.error(">>> 参数校验错误异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), argNotValidMessage);
        return AjaxResult.error(ParamExceptionEnum.PARAM_ERROR.getCode(), argNotValidMessage);
    }

    /**
     * 拦截参数校验错误异常
     *
 *
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult paramError(BindException e) {
        String argNotValidMessage = getArgNotValidMessage(e.getBindingResult());
        log.error(">>> 参数校验错误异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), argNotValidMessage);
        return AjaxResult.error(ParamExceptionEnum.PARAM_ERROR.getCode(), argNotValidMessage);
    }


    /**
     * 拦截权限异常
     *
 *
     */
    @ExceptionHandler(PermissionException.class)
    public AjaxResult noPermission(PermissionException e) {
        log.error(">>> 权限异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage() + "，请求地址为:" + HttpServletTool.getRequest().getRequestURI());
        return AjaxResult.error(401, e.getMessage() + "，请求地址为:" + HttpServletTool.getRequest().getRequestURI());
    }

    /**
     * 拦截业务异常
     *
 *
     */
    @ExceptionHandler({CodeException.class})
    public AjaxResult codeException(CodeException e, HttpServletResponse response) {
        log.error(">>> ServiceException 业务异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
        if (systemProperties.isPrintException()) {
            log.info("异常信息如下，仅开发阶段可见, 通过配置 showError 实现");
            e.printStackTrace();
        } else {
            log.error(e.getMessage());
        }

        if (e.getCode() == 401) {
             ResponseTool.responseExceptionError(response, e.getCode(), e.getMessage());
             return null;
        }

        return AjaxResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 拦截业务异常
     *
 *
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public AjaxResult assertError(RuntimeException e) {
        log.error(">>> 业务异常，assertError 请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
        if (systemProperties.isPrintException()) {
            e.printStackTrace();
        }

        return AjaxResult.error(500, e.getMessage());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public AjaxResult constraintViolationException(ConstraintViolationException e) {
        log.warn("约束异常:{}", e.getMessage());
        return AjaxResult.error(getHumanMessage(e));
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public AjaxResult dataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        log.error("数据处理异常", e);

        if (e != null && e.getCause() != null && e.getCause().getCause() != null) {
            Throwable ex = e.getCause().getCause();
            String msg = ex.getMessage();

            if (msg.contains("Data too long")) {
                return AjaxResult.error("数据长度超过限制，请修改！");
            }


            if (ex instanceof SQLIntegrityConstraintViolationException) {
                if (msg.startsWith("Duplicate")) {
                    String result = RegexTool.findFirstMatch("\\'(.*?)\\'", msg, 1);
                    if (StrUtil.isNotBlank(result)) {
                        return AjaxResult.error("数据重复,操作不能继续进行。 重复数据为：" + result);
                    }
                }

                {
                    // Column 'file_id' cannot be null
                    String regex = "Column '(.*)' cannot be null";
                    String fieldName = RegexTool.findFirstMatch(regex, msg, 1);
                    if (StrUtil.isNotEmpty(fieldName)) {
                        return AjaxResult.error("字段" + fieldName + "不能为空");
                    }
                }

            }
        }

        return AjaxResult.error("违反数据库规则，操作不能继续进行");
    }

    public static void main(String[] args) {
        String msg = "Column 'file_id' cannot be null";


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
                    return AjaxResult.error(humanMessage);
                }
            }
        }
        return AjaxResult.error("网络异常,请稍后再试");
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public AjaxResult InvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e, HttpServletRequest request) {
        e.printStackTrace();
        Throwable throwable = e.getCause();
        return AjaxResult.error(throwable.getMessage());
    }


    /**
     * 拦截未知的运行时异常
     *
 *
     */
    @ExceptionHandler(SQLException.class)
    public AjaxResult sQLException(SQLException e) {
        log.error("SQL异常", e);
        return renderJson(e);
    }

    /**
     * 拦截未知的运行时异常
     *
 *
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult serverError(Throwable e) {
        log.error(">>> 服务器运行异常，请求号为：{}", RequestNoContext.get());
        e.printStackTrace();

        log.error(e.getMessage());
        return renderJson(e);
    }

    /**
     * 渲染异常json
     *
 *
     */
    private AjaxResult renderJson(AbstractBaseExceptionEnum baseExceptionEnum) {
        return AjaxResult.error(baseExceptionEnum.getCode(), baseExceptionEnum.getMessage());
    }

    /**
     * 渲染异常json
     */
    private AjaxResult renderJson(Throwable throwable) {
        String message = throwable.getMessage();

        // 中文则提示中文，非中文则使用默认提示
        if (!StrTool.isChinese(message)) {
            message = ServerExceptionEnum.SERVER_ERROR.getMessage();
        }

        return AjaxResult.error(ServerExceptionEnum.SERVER_ERROR.getCode(), message);
    }


    /**
     * 获取请求参数不正确的提示信息
     * <p>
     * 多个信息，拼接成用逗号分隔的形式
     *
 *
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


