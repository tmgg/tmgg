

package io.tmgg.framework.data.append;


import io.tmgg.lang.HttpServletTool;
import io.tmgg.web.persistence.AutoAppendStrategy;
import io.tmgg.modules.system.service.SysFileService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AutoAppendFileViewUrlStrategy implements AutoAppendStrategy {

    @Resource
    SysFileService service;

    public Object getAppendValue(Object bean, Object fileId, String params) {
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
