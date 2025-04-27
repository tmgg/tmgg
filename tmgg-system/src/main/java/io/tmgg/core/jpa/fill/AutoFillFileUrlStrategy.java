

package io.tmgg.core.jpa.fill;


import io.tmgg.lang.HttpServletTool;
import io.tmgg.lang.dao.AutoFillStrategy;
import io.tmgg.modules.sys.service.SysFileService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AutoFillFileUrlStrategy implements AutoFillStrategy {

    @Resource
    SysFileService service;

    public Object getValue(Object bean, Object fileId, String params) {
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
