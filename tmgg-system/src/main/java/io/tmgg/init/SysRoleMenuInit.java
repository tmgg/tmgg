package io.tmgg.init;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.VersionUtil;
import io.tmgg.modules.system.dao.SysMenuDao;
import io.tmgg.modules.system.dao.SysRoleDao;
import io.tmgg.modules.system.entity.SysMenu;
import io.tmgg.modules.system.entity.SysRole;
import io.tmgg.modules.system.service.SysMenuService;
import io.tmgg.web.db.DbCacheDao;
import io.tmgg.web.persistence.BaseEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.tmgg.init.GlobalSystemDataInit.CACHE_KEY_FRAMEWORK_VERSION;

/**
 * 针对1.1.60之前版本的升级
 */
@Slf4j
public class SysRoleMenuInit implements CommandLineRunner {

    @Resource
    private SysRoleDao sysRoleDao;


    @Resource
    private DbCacheDao dbCacheDao;

    @Resource
    private SysMenuService sysMenuService;


    @Override
    public void run(String... args) throws Exception {
        String cacheVersion = dbCacheDao.get(CACHE_KEY_FRAMEWORK_VERSION);
        if (cacheVersion == null || VersionUtil.isLessThan(cacheVersion, "1.1.61")) {
            fixData();
            log.warn("角色数据升级");
        }
    }

    private void fixData() {
        List<SysRole> roles = sysRoleDao.findAll();
        for (SysRole role : roles) {
            List<String> perms = role.getPerms();
            if (CollUtil.isEmpty(perms)) {
                continue;
            }

            List<SysMenu> menus = sysMenuService.findByPerms(perms);


            List<String> ids = menus.stream().map(BaseEntity::getId).toList();
            List<String> pidList = sysMenuService.findPidList(ids);
            Set<String> allIds = new HashSet<>();
            allIds.addAll(ids);
            pidList.addAll(pidList);


            menus = sysMenuService.findAllById(allIds);

            role.setMenus(menus);
            sysRoleDao.save(role);
        }

    }
}
