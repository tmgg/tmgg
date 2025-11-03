package io.tmgg.init;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.VersionUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import io.tmgg.Build;
import io.tmgg.config.SysProp;
import io.tmgg.dbtool.DbTool;
import io.tmgg.framework.dict.DictAnnHandler;
import io.tmgg.framework.dict.DictFieldAnnHandler;
import io.tmgg.framework.perm.PermissionService;
import io.tmgg.lang.PasswordTool;
import io.tmgg.modules.system.Configs;
import io.tmgg.modules.system.dao.SysConfigDao;
import io.tmgg.modules.system.dao.SysUserDao;
import io.tmgg.modules.system.entity.DataPermType;
import io.tmgg.modules.system.entity.SysConfig;
import io.tmgg.modules.system.entity.SysRole;
import io.tmgg.modules.system.entity.SysUser;
import io.tmgg.modules.system.service.JsonEntityService;
import io.tmgg.modules.system.service.SysMenuService;
import io.tmgg.modules.system.service.SysRoleService;
import io.tmgg.web.db.DbCacheDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 系统数据初始化
 */
@Slf4j
@Component(GlobalSystemDataInit.BEAN_NAME)
@Order(0)
public class GlobalSystemDataInit implements CommandLineRunner {


    public static final String BEAN_NAME = "sysInit";

    public static final String CACHE_KEY_FRAMEWORK_VERSION = "FRAMEWORK_VERSION";

    @Resource
    SysRoleService sysRoleService;

    @Resource
    SysUserDao sysUserDao;


    @Resource
    SysConfigDao sysConfigDao;

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

    @Resource
    SysProp sysProp;

    @Value("${spring.application.name}")
    String applicationName;

    @Resource
    private SysMenuInit sysMenuInit;
    @Resource
    private SystemHookService systemHookService;
    @Override
    public void run(String... args) throws Exception {
        systemHookService.trigger(SystemHookEventType.BEFORE_DATA_INIT);


        log.info("框架版本 {}", Build.getFrameworkVersion());
        log.info("框架构建时间 {}", Build.getFrameworkBuildTime());

        String cacheVersion = dbCacheDao.get(CACHE_KEY_FRAMEWORK_VERSION);
        log.info("上次启动的框架版本号:{}", cacheVersion);


        log.info("执行初始化程序： {}", getClass().getName());
        long time = System.currentTimeMillis();


        if (cacheVersion == null || VersionUtil.isLessThan(cacheVersion,"0.3.91")) {
            fixDict();
        }

        permissionService.init();
        dictAnnHandler.run();
        dictFieldAnnHandler.run();
        jsonEntityService.initOnStartup();
        sysMenuInit.init();
        systemHookService.trigger(SystemHookEventType.AFTER_SYSTEM_MENU_INIT);

        SysRole adminRole = sysRoleService.initDefaultAdmin();
        initUser(adminRole);

        initSysConfig();




        log.info("数据初始化完成，缓存框架版本号");
        dbCacheDao.set(CACHE_KEY_FRAMEWORK_VERSION, Build.getFrameworkVersion());

        log.info("系统初始化耗时：{}", System.currentTimeMillis() - time);

        systemHookService.trigger(SystemHookEventType.AFTER_DATA_INIT);
    }

    private void initSysConfig() {
        log.info("随机生成RSA的公私钥");
        SysConfig pub = sysConfigDao.findOne(Configs.RSA_PUBLIC_KEY);
        if (pub == null) {
            RSA rsa = SecureUtil.rsa();
            sysConfigDao.addDefault("RSA公钥", Configs.RSA_PUBLIC_KEY, rsa.getPublicKeyBase64(), "password"); // 放到siteInfo, 前端可获取
            sysConfigDao.addDefault("RSA私钥", Configs.RSA_PRIVATE_KEY, rsa.getPrivateKeyBase64(), "password");
        }

        sysConfigDao.addDefault("默认密码", "sys.default.password", PasswordTool.random(), "password");


        sysConfigDao.cleanCache();
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
        log.info("-------------------------------------------");
        log.info("初始化管理员中....");
        String id = "admin";
        SysUser admin = sysUserDao.findOne(id);
        String account = "admin-" + applicationName;
        String pwd = PasswordTool.random();


        if (admin == null) {
            admin = new SysUser();
            admin.setId(id);
            admin.setAccount(account);
            admin.setName("管理员");
            admin.setEnabled(true);
            admin.getRoles().add(adminRole);
            admin.setDataPermType(DataPermType.ALL);
            admin.setPassword(PasswordTool.encode(pwd));
            admin = sysUserDao.save(admin);
            log.info("创建默认管理员 {}", admin.getAccount());

            dbCacheDao.set("admin_default_pwd", pwd);
            log.info("默认密码为： {}", pwd);
        }
        log.info("管理员登录账号:{}", admin.getAccount());
        log.info("默认密码:{}， 请尽快修改", pwd);

        if(sysProp.isResetAdminPwd()){
            admin.setPassword(PasswordTool.encode(pwd));
            log.info("管理员密码重置为 {}", pwd);
            sysUserDao.save(admin);
        }

        log.info("-------------------------------------------");
    }


}
