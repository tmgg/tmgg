package io.tmgg.modules.openapi.defaults;

import io.tmgg.modules.openapi.OpenApi;
import io.tmgg.modules.openapi.OpenApiField;
import io.tmgg.modules.openapi.gateway.BaseOpenApi;
import io.tmgg.modules.sys.entity.SysUser;
import io.tmgg.modules.sys.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class UserApi implements BaseOpenApi {

    @Resource
    SysUserService sysUserService;


    @OpenApi(name = "验证账号密码", action = "user.checkPwd")
    public boolean checkPwd(@OpenApiField(desc = "账号") String account, @OpenApiField(desc = "密码") String pwd) {
        try {
            SysUser sysUser = sysUserService.checkPwd(account, pwd);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @OpenApi(name = "获取账号名称", action = "user.name", desc = "通过账号获取账号名称")
    public String username(@OpenApiField(desc = "账号") String account) {
        SysUser sysUser = sysUserService.findByAccount(account);
        Assert.notNull(sysUser,"用户不存在");
        return sysUser.getName();
    }

}
