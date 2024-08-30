package io.tmgg.core.jpa.fill;

import io.tmgg.lang.dao.AutoFillStrategy;
import io.tmgg.sys.dao.SysUserDao;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class AutoFillUserLabelStrategy implements AutoFillStrategy {


    @Resource
    SysUserDao dao;



    @Override
    public Object getValue(Object bean, Object sourceValue, String param) {
        String id = (String) sourceValue;


        if(id == null){
            return null;
        }

        return  dao.getNameById(id);
    }
}
