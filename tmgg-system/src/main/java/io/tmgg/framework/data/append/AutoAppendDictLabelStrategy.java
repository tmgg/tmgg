

package io.tmgg.framework.data.append;


import io.tmgg.web.persistence.AutoAppendStrategy;
import io.tmgg.modules.sys.service.SysDictService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class AutoAppendDictLabelStrategy implements AutoAppendStrategy {

    @Resource
    SysDictService service;

    public Object getAppendValue(Object bean, Object sourceValue, String typeCode) {
        Assert.state(typeCode != null, "数字字典的typeCode不能为空，请设置 param参数");

        if(sourceValue == null){
            return null;
        }
        String key = String.valueOf(sourceValue);


        return service.findTextByDictCodeAndKey(typeCode, key);
    }
}
