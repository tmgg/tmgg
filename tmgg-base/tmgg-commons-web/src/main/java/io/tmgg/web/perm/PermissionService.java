package io.tmgg.web.perm;

import cn.hutool.core.util.StrUtil;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.util.pattern.PathPattern;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermissionService {

    public String parsePerm(HandlerMethod handlerMethod, RequestMappingInfo info) {

        Set<String> patterns = info.getPathPatternsCondition().getPatterns().stream().map(PathPattern::getPatternString).collect(Collectors.toSet());
        // Assert.state(patterns.size() == 1, "未指定 " + HasPermission.class.getSimpleName() + "的value时，url只能设置一个");

        String url = patterns.iterator().next();

        return parsePerm(handlerMethod, url);
    }

    public String parsePerm(HandlerMethod handlerMethod, String url) {
        HasPermission hasPermission = handlerMethod.getMethodAnnotation(HasPermission.class);
        if(hasPermission == null){
            return null;
        }
        String perm = hasPermission.value();

        if (StrUtil.isNotEmpty(perm)) {
            return perm;
        }


        // 将url 的斜杠转为冒号
        url = StrUtil.removePrefix(url, "/");
        url = StrUtil.removeSuffix(url, "/");

        perm = url.replaceAll("/", ":");

        return perm;
    }

}
