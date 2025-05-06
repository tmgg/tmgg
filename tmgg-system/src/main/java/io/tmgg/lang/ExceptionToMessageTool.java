
package io.tmgg.lang;

import cn.hutool.core.util.StrUtil;
import jakarta.persistence.RollbackException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Set;


/**
 * 将异常转换为友好的消息
 */
@Slf4j
public class ExceptionToMessageTool {

    public static String convert(Throwable throwable) {
        String message = dispatch(throwable);

        // 中文则提示中文，非中文则使用默认提示
        if (!StrTool.isChinese(message)) {
            message = "服务器忙";
        }
        return message;
    }
    private static String dispatch(Throwable throwable) {
        if (throwable instanceof ConstraintViolationException e) {
            return convert(e);
        }


        if (throwable instanceof DataIntegrityViolationException e) {
            return convert(e);
        }
        if(throwable instanceof TransactionSystemException e){
            return convert(e);
        }


        return throwable.getMessage();
    }

    @NotNull
    private static String convert(DataIntegrityViolationException e) {
        if (e.getCause() != null && e.getCause().getCause() != null) {
            Throwable ex = e.getCause().getCause();
            String msg = ex.getMessage();

            if (msg.contains("Data too long")) {
                return "数据长度超过限制，请修改！";
            }


            if (ex instanceof SQLIntegrityConstraintViolationException) {
                if (msg.startsWith("Duplicate")) {
                    String result = RegexTool.findFirstMatch("\\'(.*?)\\'", msg, 1);
                    if (StrUtil.isNotBlank(result)) {
                        return "数据重复,操作不能继续进行。 重复数据为：" + result;
                    }
                }

                {
                    // Column 'file_id' cannot be null
                    String regex = "Column '(.*)' cannot be null";
                    String fieldName = RegexTool.findFirstMatch(regex, msg, 1);
                    if (StrUtil.isNotEmpty(fieldName)) {
                        return "字段" + fieldName + "不能为空";
                    }
                }

            }
        }
        return "数据之间有关联约束";
    }

    private static String convert(ConstraintViolationException e) {
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


    private static String convert(TransactionSystemException e) {
        if (e.getCause() != null) {
            RollbackException cause = (RollbackException) e.getCause();
            if (cause != null) {
                Throwable cause2 = cause.getCause();
                if (cause2 instanceof ConstraintViolationException) {
                    return convert((ConstraintViolationException) cause2);
                }
            }
        }
        return "执行事务异常" + e.getMessage();
    }
}


