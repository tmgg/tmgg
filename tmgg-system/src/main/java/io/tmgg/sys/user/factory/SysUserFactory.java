
package io.tmgg.sys.user.factory;

import io.tmgg.web.enums.AdminType;
import io.tmgg.web.enums.CommonStatus;
import io.tmgg.sys.consts.service.SysConfigService;
import io.tmgg.sys.user.entity.SysUser;
import io.tmgg.lang.PasswordTool;
import cn.hutool.core.text.PasswdStrength;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.util.Assert;


/**
 * 填充用户附加信息工厂
 *

 *
 */
public class SysUserFactory {

    /**
     * 管理员类型（1超级管理员 2非管理员）
     * 新增普通用户时填充相关信息
     *

 *
     */
    public static void fillAddCommonUserInfo(SysUser sysUser) {
        fillBaseUserInfo(sysUser);
        sysUser.setAdminType(AdminType.NONE);
    }

    /**
     * 填充用户基本字段
     *

 *
     */
    public static void fillBaseUserInfo(SysUser sysUser) {
        //密码为空则设置密码
        if (ObjectUtil.isEmpty(sysUser.getPassword())) {
            //没有密码则设置默认密码
            String password = SpringUtil.getBean(SysConfigService.class).getDefaultPassWord();
            PasswdStrength.PASSWD_LEVEL level = PasswdStrength.getLevel(password);
            Assert.state(level != PasswdStrength.PASSWD_LEVEL.EASY, "默认密码强度过低，请先修改系统配置" );
            //设置密码为Md5加密后的密码
            sysUser.setPassword(PasswordTool.encode(password));
        }



        sysUser.setStatus(CommonStatus.ENABLE);
    }
}
