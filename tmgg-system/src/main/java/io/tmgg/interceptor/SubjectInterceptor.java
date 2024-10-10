package io.tmgg.interceptor;


import io.tmgg.lang.SpringTool;
import io.tmgg.lang.ann.PublicApi;
import io.tmgg.web.perm.AuthorizingRealm;
import io.tmgg.web.perm.SecurityManager;
import io.tmgg.web.perm.Subject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Collection;

/**
 * 设置登录用户，
 * <p>
 * 注意：如果是app的客户，非SysUser表，最好另建拦截器, jwt的发布者设为app以区分
 */
@Slf4j
public class SubjectInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // eg: /sysOrg/page
        String uri = request.getRequestURI();

        // 静态资源，忽略
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }


        initReams();

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            if (hm.hasMethodAnnotation(PublicApi.class)) {
                return true;
            }
        }


        String userId = (String) request.getSession().getAttribute("subjectId");


        for (AuthorizingRealm realm : realmList) {
            if (!uri.startsWith(realm.prefix())) {
                continue;
            }
            Subject subject = realm.doGetSubject(userId);
            realm.doGetPermissionInfo(subject);
            request.getSession().setAttribute(SecurityManager.SESSION_KEY, subject);
            return true;
        }
        return true;


    }

    private void initReams() {
        if (realmList == null) {
            Collection<AuthorizingRealm> realms = SpringTool.getBeans(AuthorizingRealm.class);
            realmList = new AuthorizingRealm[realms.size()];
            realms.toArray(realmList);
        }
    }


    private AuthorizingRealm[] realmList;

}
