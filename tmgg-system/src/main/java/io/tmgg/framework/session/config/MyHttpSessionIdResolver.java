package io.tmgg.framework.session.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.util.List;

/**
 * 在cookie session 的基础上，支持header
 * <p>
 * CookieHttpSessionIdResolver
 */


public class MyHttpSessionIdResolver implements HttpSessionIdResolver {

    CookieHttpSessionIdResolver resolver = new CookieHttpSessionIdResolver();
    HeaderHttpSessionIdResolver appResolver = HeaderHttpSessionIdResolver.xAuthToken();


    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        return get(request).resolveSessionIds(request);
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
         get(request).setSessionId(request,response, sessionId);
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        get(request).expireSession(request, response);
    }

    private HttpSessionIdResolver get(HttpServletRequest request){
        return  isApp(request) ? appResolver: resolver;
    }

    private boolean isApp(HttpServletRequest request) {
        String header = request.getHeader("X-Client-Type");
        return "app".equals(header);
    }
}
