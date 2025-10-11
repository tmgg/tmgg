package io.tmgg.flowable;

import io.tmgg.lang.SpringTool;
import io.tmgg.modules.system.dao.SysUserDao;
import io.tmgg.modules.system.entity.SysUser;
import jakarta.annotation.Resource;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

public class TestJavaDelegate implements JavaDelegate {



    @Override
    public void execute(DelegateExecution execution) {
        SysUserDao sysUserDao = SpringTool.getBean(SysUserDao.class);
        List<SysUser> list = sysUserDao.findAll();
        execution.setVariable("userList", list);
        execution.setVariable("userCount", list.size());

    }
}
