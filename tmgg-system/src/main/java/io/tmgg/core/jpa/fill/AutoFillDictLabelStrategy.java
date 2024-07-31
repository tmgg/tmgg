package io.tmgg.core.jpa.fill;

import io.tmgg.lang.dao.AutoFillStrategy;
import io.tmgg.sys.dict.service.SysDictDataService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;

@Component
public class AutoFillDictLabelStrategy implements AutoFillStrategy {

    @Resource
    SysDictDataService service;

    @Override
    public Object getValue(Object bean, Object sourceValue,String param) {
        Assert.state(param != null, "数字字典的typeCode不能为空，请设置 param参数");

        String code = String.valueOf(sourceValue) ;


        if(code != null){
            return service.getValue(param, code);
        }

        return null;
    }


}
