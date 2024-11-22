

package io.tmgg.core.jpa.fill;


import io.tmgg.lang.dao.AutoFillStrategy;
import io.tmgg.sys.service.SysDictService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class AutoFillDictLabelAutoFillStrategy implements AutoFillStrategy {

    @Resource
    SysDictService service;

    public Object getValue(Object bean, Object sourceValue, String typeCode) {
        Assert.state(typeCode != null, "数字字典的typeCode不能为空，请设置 param参数");

        String key = String.valueOf(sourceValue);
        if(key == null){
            return null;
        }

        return service.findTextByDictCodeAndKey(typeCode, key);
    }
}
