package io.tmgg.framework.data.append;

import io.tmgg.web.persistence.AutoAppendStrategy;
import io.tmgg.modules.system.dao.SysUserDao;
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
