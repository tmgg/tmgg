package io.tmgg.web.perm;

import io.tmgg.lang.RequestTool;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 参考了shiro 的权限管理
 */
@Slf4j
public class SecurityUtils {


    public static final String SESSION_KEY = "SUBJECT";

    /**
     * 获取当前的用户
     */
    public static Subject getSubject() {
        HttpServletRequest request = RequestTool.currentRequest();
        if (request == null) {
            return null;
        }
        HttpSession session = request.getSession();
        if (session == null) {
            return null;
        }

        return (Subject) session.getAttribute(SESSION_KEY);
    }


}
