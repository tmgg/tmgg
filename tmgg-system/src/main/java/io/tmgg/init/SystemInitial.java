package io.tmgg.init;

import cn.hutool.core.util.StrUtil;
import io.tmgg.SysProp;
import io.tmgg.framework.dict.DictAnnHandler;
import io.tmgg.lang.PasswordTool;
import io.tmgg.sys.dao.SysUserDao;
import io.tmgg.sys.entity.SysRole;
import io.tmgg.sys.entity.SysUser;
import io.tmgg.sys.perm.SysMenuService;
import io.tmgg.sys.service.SysConfigService;
import io.tmgg.sys.service.SysRoleService;
import io.tmgg.sys.entity.DataPermType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
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
    DictAnnHandler dictAnnHandler;



    @Resource
    PermissionToDatabaseHandler permissionToDatabaseHandler;

    @Resource
    SysProp sysProp;

    @Resource
    SystemUpgradeInit systemUpgradeInit;

    @Override
    public void run(String... args) throws Exception {
        log.info("开支执行系统初始化程序： {}", getClass().getName());
        if(!sysProp.isAutoUpdateSysData()){
            log.info("自动更新系统数据已关闭");
            return;
        }

        systemUpgradeInit.init();;

        dictEnumHandler.run();
        dictAnnHandler.run();

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
            admin.setEnabled(true);
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
