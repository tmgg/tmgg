package io.tmgg.modules.sys.controller;

import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.TreeOption;
import io.tmgg.modules.sys.entity.SysMenu;
import io.tmgg.modules.sys.entity.SysMenuBadge;
import io.tmgg.modules.sys.service.SysMenuBadgeService;
import io.tmgg.modules.sys.service.SysMenuService;
import io.tmgg.web.annotion.HasPermission;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("sysMenuBadge")
public class SysMenuBadgeController  extends BaseController<SysMenuBadge>{

    @Resource
    SysMenuBadgeService service;

    @Resource
    SysMenuService sysMenuService;




    @HasPermission
    @PostMapping("page")
    public AjaxResult page( @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {

        Map<String, SysMenu> menuMap = sysMenuService.findMenuMap();

        Page<SysMenuBadge> page = service.findAll( pageable);

        for (SysMenuBadge m : page) {
            String menuId = m.getMenuId();
            SysMenu sysMenu = menuMap.get(menuId);
            if(sysMenu !=null){
                m.setMenuName(sysMenu.getName());
            }
        }

        return AjaxResult.ok().data(page);
    }



    @GetMapping("menuOptions")
    public AjaxResult menuOptions() throws Exception {
        List<SysMenu> menus = sysMenuService.findMenuVisible();

        List<TreeOption> treeOptions = TreeOption.convertTree(menus, BaseEntity::getId, SysMenu::getPid, SysMenu::getName);

        return AjaxResult.ok().data(treeOptions);
    }
}

