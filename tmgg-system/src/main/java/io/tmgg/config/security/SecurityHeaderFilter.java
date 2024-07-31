package io.tmgg.config.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/***
 *
 */
public class SecurityHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;

        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Frame-Options
        resp.setHeader("X-Frame-Options", "SAMEORIGIN");

        // CSP问题
        resp.setHeader("Content-Security-Policy",
                "img-src 'self' data: ; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                "style-src 'self' 'unsafe-inline'; frame-ancestors 'self'; " +
                "font-src 'self' data:; " +
                "form-action 'self'; " +
                "base-uri 'self'; " +
                "object-src 'none'; " +
                "report-uri 'self'");

        // 这行代码将阻止任何源（包括顶层页面和 <iframe>）使用任何受权限策略控制的特性。星号 * 表示所有特性，而空列表 () 表示没有任何源被允许使用这些特性
        resp.setHeader("Permissions-Policy", "*");

        // XContentTypeOptionsFilter
        resp.setHeader("X-Content-Type-Options", "nosniff");

        // https HTS
        resp.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");

        chain.doFilter(request, response);
    }


}
