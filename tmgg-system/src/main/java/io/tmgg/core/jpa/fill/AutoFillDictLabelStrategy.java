

package io.tmgg.core.jpa.fill;


import io.tmgg.lang.dao.AutoFillStrategy;
import io.tmgg.sys.service.SysDictService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class AutoFillDictLabelStrategy implements AutoFillStrategy {

    @Resource
    SysDictService service;

    public Object getValue(Object bean, Object sourceValue, String typeCode) {
        Assert.state(typeCode != null, "数字字典的typeCode不能为空，请设置 param参数");

        if(sourceValue == null){
            return null;
        }
        String key = String.valueOf(sourceValue);


        return service.findTextByDictCodeAndKey(typeCode, key);
    }
}
