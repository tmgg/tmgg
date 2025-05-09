package io.tmgg.core.jpa.fill;

import io.tmgg.lang.dao.AutoAppendStrategy;
import io.tmgg.modules.sys.dao.SysUserDao;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class AutoAppendUserLabelStrategy implements AutoAppendStrategy {


    @Resource
    SysUserDao dao;


    @Override
    public Object getAppendValue(Object bean, Object sourceValue, String param) {
        String id = (String) sourceValue;


        if(id == null){
            return null;
        }

        return  dao.getNameById(id);
    }
}
