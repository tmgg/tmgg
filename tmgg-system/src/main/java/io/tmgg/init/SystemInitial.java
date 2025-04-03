package io.tmgg.init;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import io.tmgg.SysProp;
import io.tmgg.dbtool.DbTool;
import io.tmgg.framework.dict.DictAnnHandler;
import io.tmgg.framework.dict.DictFieldAnnHandler;
import io.tmgg.lang.PasswordTool;
import io.tmgg.modules.sys.dao.SysUserDao;
import io.tmgg.modules.sys.entity.DataPermType;
import io.tmgg.modules.sys.entity.SysRole;
import io.tmgg.modules.sys.entity.SysUser;
import io.tmgg.modules.sys.service.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 系统初始化
 */
@Slf4j
@Component(SystemInitial.BEAN_NAME)
@Order(Ordered.LOWEST_PRECEDENCE)
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
    DictFieldAnnHandler dictFieldAnnHandler;

    @Resource
    private DbTool db;


    @Override
    public void run(String... args) throws Exception {
        log.info("执行系统初始化程序： {}", getClass().getName());
        long time = System.currentTimeMillis();


        fixDict();

        dictEnumHandler.run();
        dictAnnHandler.run();
        dictFieldAnnHandler.run();
        jsonEntityService.initOnStartup();
        sysMenuService.reset();
        SysRole adminRole = sysRoleService.initDefaultAdmin();
        initUser(adminRole);





        log.info("系统初始化耗时：{}", System.currentTimeMillis() - time );

    }

    private void fixDict() {
        String[] keys = db.getKeys("select * from sys_dict limit 1");
        System.out.println(keys);
        if(ArrayUtil.contains(keys,"name")){
            db.executeQuietly("ALTER TABLE `sys_dict` DROP COLUMN text");
            db.executeQuietly("ALTER TABLE `sys_dict` CHANGE COLUMN `name` `text` varchar(80)");
        }

        if(ArrayUtil.contains(keys,"builtin")){
            db.executeQuietly("ALTER TABLE `sys_dict` DROP COLUMN builtin");
        }

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
