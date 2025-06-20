package io.tmgg.init;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.tmgg.Build;
import io.tmgg.dbtool.DbTool;
import io.tmgg.event.SystemDataInitFinishEvent;
import io.tmgg.framework.dict.DictAnnHandler;
import io.tmgg.framework.dict.DictFieldAnnHandler;
import io.tmgg.framework.perm.PermissionService;
import io.tmgg.lang.PasswordTool;
import io.tmgg.lang.SpringTool;
import io.tmgg.modules.system.dao.SysUserDao;
import io.tmgg.modules.system.entity.DataPermType;
import io.tmgg.modules.system.entity.SysRole;
import io.tmgg.modules.system.entity.SysUser;
import io.tmgg.modules.system.service.JsonEntityService;
import io.tmgg.modules.system.service.SysConfigService;
import io.tmgg.modules.system.service.SysMenuService;
import io.tmgg.modules.system.service.SysRoleService;
import io.tmgg.web.db.DbCacheDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 系统数据初始化
 */
@Slf4j
@Component(FrameworkDataInit.BEAN_NAME)
public class FrameworkDataInit implements CommandLineRunner {


    public static final String BEAN_NAME = "sysInit";

    public static final String CACHE_KEY_FRAMEWORK_VERSION = "FRAMEWORK_VERSION";

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
    DictAnnHandler dictAnnHandler;


    @Resource
    DictFieldAnnHandler dictFieldAnnHandler;

    @Resource
    PermissionService permissionService;

    @Resource
    private DbTool db;

    @Resource
    private DbCacheDao dbCacheDao;


    @Override
    public void run(String... args) throws Exception {

        Collection<PreFrameworkDataInit> preSystemDataInits = SpringTool.getBeans(PreFrameworkDataInit.class);
        for (PreFrameworkDataInit preSystemDataInit : preSystemDataInits) {
            log.info("在框架初始化数据之前执行: {}", preSystemDataInit.getClass().getName());
            preSystemDataInit.run();
        }


        log.info("框架版本 {}", Build.getFrameworkVersion());
        log.info("框架构建时间 {}", Build.getFrameworkBuildTime());

        String cacheVersion = dbCacheDao.get(CACHE_KEY_FRAMEWORK_VERSION);
        log.info("上次启动的框架版本号:{}", cacheVersion);


        log.info("执行初始化程序： {}", getClass().getName());
        long time = System.currentTimeMillis();


        if (cacheVersion == null || cacheVersion.compareTo("0.3.91") < 0) {
            fixDict();
        }

        permissionService.init();
        dictAnnHandler.run();
        dictFieldAnnHandler.run();
        jsonEntityService.initOnStartup();
        sysMenuService.reset();
        SysRole adminRole = sysRoleService.initDefaultAdmin();
        initUser(adminRole);


        SpringUtil.publishEvent(new SystemDataInitFinishEvent(this));


        log.info("数据初始化完成，缓存框架版本号");
        dbCacheDao.set(CACHE_KEY_FRAMEWORK_VERSION, Build.getFrameworkVersion());

        log.info("系统初始化耗时：{}", System.currentTimeMillis() - time);

    }

    private void fixDict() {
        String[] keys = db.getKeys("select * from sys_dict");
        System.out.println(keys);
        if (ArrayUtil.contains(keys, "name")) {
            db.executeQuietly("ALTER TABLE `sys_dict` DROP COLUMN text");
            db.executeQuietly("ALTER TABLE `sys_dict` CHANGE COLUMN `name` `text` varchar(80)");
        }

        if (ArrayUtil.contains(keys, "builtin")) {
            db.executeQuietly("ALTER TABLE `sys_dict` DROP COLUMN builtin");
        }

    }


    private void initUser(SysRole adminRole) {
        SysUser admin = sysUserDao.findByAccount("superAdmin");

        if (admin == null) {
            log.info("创建默认管理员");
            admin = new SysUser();
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
