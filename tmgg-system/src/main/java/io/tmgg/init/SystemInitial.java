package io.tmgg.init;

import io.tmgg.lang.PasswordTool;
import io.tmgg.sys.app.service.SysConfigService;
import io.tmgg.sys.perm.SysPermService;
import io.tmgg.sys.role.entity.SysRole;
import io.tmgg.sys.role.service.SysRoleService;
import io.tmgg.sys.dao.SysUserDao;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.user.enums.DataPermType;
import io.tmgg.web.enums.CommonStatus;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import static io.tmgg.init.SystemInitial.ORDER;

/**
 * 系统初始化
 */
@Slf4j
@Component
@Order(ORDER)
public class SystemInitial implements ApplicationRunner {

    // 启动顺序
    public static final int ORDER = 1000;

    @Resource
    SysRoleService sysRoleService;

    @Resource
    SysUserDao sysUserDao;


    @Resource
    SysConfigService sysConfigService;

    @Resource
    SysPermService sysPermService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        sysConfigService.initDefault();
        autoAddDictDataRunnable.run();

        sysPermService.init();

        jsonToDatabaseHandler.run();

        permissionToDatabaseHandler.run();

        sysRoleService.initDefaultUserRole();
        SysRole adminRole = sysRoleService.initDefaultAdmin();
        initUser(adminRole);

    }


    private void initUser(SysRole adminRole) {
        SysUser admin = sysUserDao.findByAccount("superAdmin");

        if (admin == null) {
            log.info("创建默认管理员");
            admin = new SysUser();
            admin.setId("superAdmin");
            admin.setAccount("superAdmin");
            admin.setName("管理员");
            admin.setStatus(CommonStatus.ENABLE);
            admin.getRoles().add(adminRole);
            admin.setDataPermType(DataPermType.ALL);

            admin=  sysUserDao.save(admin);
        }

        if (StrUtil.isBlankIfStr(admin.getPassword())) {
            String defaultPassWord = sysConfigService.getDefaultPassWord();
            admin.setPassword(PasswordTool.encode(defaultPassWord));
            log.info("-------------------------------------------");
            log.info("管理员密码为 {}", defaultPassWord);
            log.info("-------------------------------------------");
            sysUserDao.save(admin);
        }

    }




    @Resource
    JsonToDatabaseHandler jsonToDatabaseHandler;


    @Resource
    AutoAddDictDataRunnable autoAddDictDataRunnable;


    @Resource
    PermissionToDatabaseHandler permissionToDatabaseHandler;

}
