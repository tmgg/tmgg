package io.tmgg.init;

import cn.hutool.core.util.StrUtil;
import io.tmgg.SysProp;
import io.tmgg.framework.dict.DictAnnHandler;
import io.tmgg.lang.PasswordTool;
import io.tmgg.modules.sys.dao.SysUserDao;
import io.tmgg.modules.sys.entity.DataPermType;
import io.tmgg.modules.sys.entity.SysRole;
import io.tmgg.modules.sys.entity.SysUser;
import io.tmgg.modules.sys.service.*;
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
    JsonEntityService jsonEntityService;


    @Resource
    DictEnumHandler dictEnumHandler;

    @Resource
    DictAnnHandler dictAnnHandler;



    @Resource
    SysProp sysProp;

    @Resource
    UpgradeInit upgradeInit;


    @Override
    public void run(String... args) throws Exception {
        log.info("执行系统初始化程序： {}", getClass().getName());
        long time = System.currentTimeMillis();

        dictEnumHandler.run();
        dictAnnHandler.run();
        jsonEntityService.initOnStartup();
        sysMenuService.reset();
        SysRole adminRole = sysRoleService.initDefaultAdmin();
        initUser(adminRole);

        log.info("系统初始化耗时：{}", System.currentTimeMillis() - time );

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

            admin = sysUserDao.save(admin);
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
