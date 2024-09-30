package io.tmgg.init;

import cn.hutool.core.util.StrUtil;
import io.tmgg.lang.PasswordTool;
import io.tmgg.sys.dao.SysUserDao;
import io.tmgg.sys.entity.SysRole;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.perm.SysMenuService;
import io.tmgg.sys.service.SysConfigService;
import io.tmgg.sys.service.SysRoleService;
import io.tmgg.sys.user.enums.DataPermType;
import io.tmgg.web.enums.CommonStatus;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * 系统初始化
 */
@Slf4j
@Component(SystemInitial.BEAN_NAME)
public class SystemInitial implements CommandLineRunner {



    public static final String BEAN_NAME = "sysInit";

    @Resource
    SysRoleService sysRoleService;

    @Resource
    SysUserDao sysUserDao;


    @Resource
    SysConfigService sysConfigService;

    @Resource
    SysMenuService sysMenuService;

    @Resource
    JsonToDatabaseHandler jsonToDatabaseHandler;


    @Resource
    DictEnumHandler dictEnumHandler;



    @Resource
    PermissionToDatabaseHandler permissionToDatabaseHandler;

    @Override
    public void run(String... args) throws Exception {
        dictEnumHandler.start();

        sysMenuService.init();

        jsonToDatabaseHandler.run();

        permissionToDatabaseHandler.run();

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





}
