package io.tmgg.modules.system.service;

import io.tmgg.modules.system.entity.SysMenu;
import io.tmgg.modules.system.entity.SysMenuBadge;
import io.tmgg.web.persistence.BaseService;
import io.tmgg.data.query.JpaQuery;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysMenuBadgeService extends BaseService<SysMenuBadge> {

    @Resource
    SysMenuService sysMenuService;

    @Override
    public Page<SysMenuBadge> findAllByClient(JpaQuery<SysMenuBadge> q, Pageable pageable) {
        Map<String, SysMenu> menuMap = sysMenuService.findMenuMap();

        Page<SysMenuBadge> page = this.findAll( pageable);

        for (SysMenuBadge m : page) {
            String menuId = m.getMenuId();
            SysMenu sysMenu = menuMap.get(menuId);
            if(sysMenu !=null){
                m.setMenuName(sysMenu.getName());
            }
        }
        return super.findAllByClient(q, pageable);
    }
}

