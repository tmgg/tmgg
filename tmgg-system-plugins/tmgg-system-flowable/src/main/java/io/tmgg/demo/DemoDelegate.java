package io.tmgg.demo;

import cn.hutool.core.lang.Dict;
import io.tmgg.lang.ann.Remark;
import io.tmgg.modules.system.entity.SysUser;
import io.tmgg.modules.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Remark("demo-设置用户列表")
@Component
public class DemoDelegate implements JavaDelegate {

    @Resource
    SysUserService sysUserService;

    @Override
    public void execute(DelegateExecution execution) {
        List<SysUser> list = sysUserService.findAll();
        List<Dict> userList = list.stream().map(u -> Dict.of("id", u.getId(), "name", u.getName())).collect(Collectors.toList());
        execution.setVariable("userList", userList);
    }
}
