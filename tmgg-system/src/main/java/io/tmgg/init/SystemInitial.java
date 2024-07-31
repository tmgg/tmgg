package io.tmgg.init;

import io.tmgg.lang.PasswordTool;
import io.tmgg.sys.role.service.SysRoleService;
import io.tmgg.sys.user.dao.SysUserDao;
import io.tmgg.sys.user.entity.SysUser;
import io.tmgg.web.enums.AdminType;
import io.tmgg.web.enums.CommonStatus;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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


    @Override
    public void run(ApplicationArguments args) throws Exception {
        autoAddDictDataRunnable.run();


        cleanDataRunnable.run();

        databaseDataXmlInitRunnable.run();
        databaseDataJsonInitRunnable.run();

        menuInCodeRunnable.run();

        sysRoleService.createDefault();

        initUser();

    }

    private void initUser() {
        SysUser admin = sysUserDao.findByAccount("superAdmin");
        String randomPwd = RandomUtil.randomString(12);
        if (admin == null) {
            log.info("创建默认管理员");
            admin = new SysUser();
            admin.setId("superAdmin");
            admin.setAccount("superAdmin");
            admin.setName("管理员");
            admin.setStatus(CommonStatus.ENABLE);
            admin.setAdminType(AdminType.SUPER_ADMIN);
            admin.setPassword(PasswordTool.encode(randomPwd));
            log.info("-------------------------------------------");
            log.info("默认管理员密码为 {}", randomPwd);
            log.info("-------------------------------------------");
            sysUserDao.save(admin);
        } else if (StrUtil.isBlankIfStr(admin.getPassword())) {
            admin.setPassword(PasswordTool.encode(randomPwd));
            log.info("-------------------------------------------");
            log.info("管理员密码为空，重置为 {}",randomPwd);
            log.info("-------------------------------------------");
            sysUserDao.save(admin);
        }
    }

    @Resource
    CleanDataRunnable cleanDataRunnable;

    @Resource
    DatabaseDataXmlInitRunnable databaseDataXmlInitRunnable;

    @Resource
    DatabaseDataJsonInitRunnable databaseDataJsonInitRunnable;


    @Resource
    AutoAddDictDataRunnable autoAddDictDataRunnable;


    @Resource
    MenuInCodeRunnable menuInCodeRunnable;

}
