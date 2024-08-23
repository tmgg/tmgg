package io.tmgg.interceptor;


import io.tmgg.lang.SpringTool;
import io.tmgg.lang.ann.PublicApi;
import io.tmgg.web.perm.AuthorizingRealm;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import io.tmgg.web.token.TokenManger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * 设置登录用户，
 * <p>
 * 注意：如果是app的客户，非SysUser表，最好另建拦截器, jwt的发布者设为app以区分
 */
@Slf4j
@Component
public class SetUserInfoInterceptor implements HandlerInterceptor {

    @Resource
    TokenManger tokenManger;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // eg: /sysOrg/page
        String uri = request.getRequestURI();

        // 静态资源，忽略
        if(handler instanceof ResourceHttpRequestHandler){
            return true;
        }


        initReams();

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            if (hm.hasMethodAnnotation(PublicApi.class)) {
                return true;
            }
        }

        String token = tokenManger.getTokenFromRequest(request);
        synchronized (token) {
            Subject cachedSubject = SecurityUtils.getCachedSubjectByToken(token);
            if (cachedSubject != null) {
                // 直接将缓存的用户信息设置下
                SecurityUtils.login(token, cachedSubject);
                return true;
            }

            for (AuthorizingRealm realm : realmArr) {
                if (!uri.startsWith(realm.prefix())) {
                    continue;
                }
                Subject subject = realm.doGetSubject(token);
                SecurityUtils.login(token, subject);
                realm.doGetPermissionInfo(subject);
                return true;
            }
            return true;
        }


    }

    private void initReams() {
        if (realmArr == null) {
            Collection<AuthorizingRealm> realms = SpringTool.getBeans(AuthorizingRealm.class);
            realmArr = new AuthorizingRealm[realms.size()];
            realms.toArray(realmArr);
        }
    }


    private AuthorizingRealm[] realmArr;

}
