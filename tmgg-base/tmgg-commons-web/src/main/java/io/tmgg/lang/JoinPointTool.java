
package io.tmgg.lang;

import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.multipart.MultipartFile;


/**
 * Spring切面工具类
 *
 */
public class JoinPointTool {

    /**
     * 获取切面的参数json
     * @param joinPoint 切点
     * @return json
     */
    public static String getArgsJsonString(JoinPoint joinPoint) {
        StringBuilder argsJson = new StringBuilder();
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (!isFilterObject(arg)) {
                if (ObjectUtil.isNotNull(arg)) {
                    String jsonStr = JsonTool.toPrettyJsonQuietly(arg);
                    argsJson.append(jsonStr).append(" ");
                }
            }
        }
        return argsJson.toString().trim();
    }

    /**
     * 判断是否需要拼接参数，过滤掉HttpServletRequest,MultipartFile,HttpServletResponse等类型参数
     *
     */
    private static boolean isFilterObject(Object arg) {
        return arg instanceof MultipartFile || arg instanceof HttpServletRequest || arg instanceof HttpServletResponse;
    }

}
