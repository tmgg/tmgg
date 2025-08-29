

package io.tmgg.framework.data.append;


import io.tmgg.lang.HttpServletTool;
import io.tmgg.web.persistence.fill.ValueConvertStrategy;
import io.tmgg.modules.system.service.SysFileService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AutoAppendFileViewUrlStrategy implements ValueConvertStrategy {

    @Resource
    SysFileService service;

    public Object convertValue(Object bean, Object fileId, String params) {
        if(fileId == null){
            return null;
        }

        HttpServletRequest request = HttpServletTool.getRequest();
        if(request == null){
            return null;
        }


        return service.getPreviewUrl(String.valueOf(fileId),request);
    }
}
