package io.tmgg.web.argument.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 支持前端指定size=-1时，Pageable转为无分页的对象
 */
public class UnpagedPageableArgumentResolver extends PageableHandlerMethodArgumentResolver {


    public UnpagedPageableArgumentResolver() {
        setOneIndexedParameters(true);
    }

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter,
                                   ModelAndViewContainer mavContainer,
                                   NativeWebRequest webRequest,
                                   WebDataBinderFactory binderFactory) {


        // 使用默认的分页逻辑
        Pageable pageable = super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        // 获取exportExcel参数
        String size = webRequest.getParameter("size");

        // 如果exportExcel=true，返回Unpaged实例
        if ("-1".equals(size)) {
            return Pageable.unpaged(pageable.getSort());
        }

        return pageable;
    }


}
