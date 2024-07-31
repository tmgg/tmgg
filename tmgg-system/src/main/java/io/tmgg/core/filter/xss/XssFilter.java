
package io.tmgg.core.filter.xss;


import io.tmgg.SystemProperties;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * xss过滤器
 */
@Slf4j
@Component
public class XssFilter implements Filter {

    @Resource
    private SystemProperties systemProperties;

    private final Set<String> excludePathList = new HashSet<>();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        List<String> list = systemProperties.getXssExcludePathList();
        if (CollUtil.isNotEmpty(list)) {
            excludePathList.addAll(list);
        }
    }

    public void excludePath(String path) {
        excludePathList.add(path);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String servletPath = httpServletRequest.getServletPath();

        boolean exclude = isExclude(servletPath);

        if (exclude) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
        }
    }

    /**
     * 判断是否排除xss
     *
     *
     */
    private boolean isExclude(String servletPath) {
        if (CollUtil.isEmpty(excludePathList)) {
            return false;
        }

        AntPathMatcher matcher = new AntPathMatcher();
        for (String path : excludePathList) {
            if (matcher.match(path, servletPath)) {
                return true;
            }
        }

        return false;
    }

}
