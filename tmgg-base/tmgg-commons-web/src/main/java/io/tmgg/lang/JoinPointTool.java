
package io.tmgg.lang;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


/**
 * Spring切面工具类
 */
public class JoinPointTool {

    private static final ObjectMapper OBJECT_MAPPER =      JsonTool.getObjectMapper().copy().setSerializationInclusion(JsonInclude.Include.NON_NULL);


    /**
     * 获取切面的参数json
     *
     * @param joinPoint 切点
     * @return json
     */
    public static String getArgsJsonString(JoinPoint joinPoint) {
        StringBuilder argsJson = new StringBuilder();
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (ignore(arg)) {
                continue;
            }
            String curStr = getString(arg);
            argsJson.append(curStr).append(" ");
        }
        return argsJson.toString().trim();
    }

    private static String getString(Object arg) {

        try {

            return OBJECT_MAPPER.writeValueAsString(arg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否需要拼接参数，过滤掉HttpServletRequest,MultipartFile,HttpServletResponse等类型参数
     */
    private static boolean ignore(Object arg) {
        if (arg == null) {
            return true;
        }
        if (arg instanceof String && StrUtil.isBlank((String) arg)) {
            return true;
        }
        if (arg instanceof MultipartFile || arg instanceof Pageable) {
            return true;
        }


        return false;
    }

}
